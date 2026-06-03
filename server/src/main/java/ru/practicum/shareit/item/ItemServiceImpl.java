package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.shared.error.BadRequestException;
import ru.practicum.shareit.shared.error.ForbiddenException;
import ru.practicum.shareit.shared.error.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public List<ResponseItemDto> getItemsByOwner(Long ownerId) {
        getUserByIdOrThrow(ownerId);
        List<Item> items = itemRepository
                .findAllWithFetchedComments(ownerId);
        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .toList();

        List<Booking> approvedBookingsOfUserDescByStart = bookingRepository
                .findAllByItemIdsAndStatusApproved(itemIds);
        HashMap<Long, List<Booking>> bByItems = attachBookingsToItem(approvedBookingsOfUserDescByStart);

        List<Booking> approvedBookingsOnlyInFuture = approvedBookingsOfUserDescByStart != null
                ? approvedBookingsOfUserDescByStart.stream()
                    .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                    .toList()
                : null;
        HashMap<Long, List<Booking>> bInFutureByItems = attachBookingsToItem(approvedBookingsOnlyInFuture);

        List<ResponseItemDto> result = items.stream()
                .map(i -> {
                    Long itemId = i.getId();

                    List<Booking> bOfItem = bByItems != null ? bByItems.get(itemId) : null;
                    List<Booking> bInFutureOfItem = bInFutureByItems != null ? bInFutureByItems.get(itemId) : null;

                    Booking lastBooking = bOfItem != null ? bOfItem.getFirst() : null;
                    Booking nextBooking = bInFutureOfItem != null ? bInFutureOfItem.getLast() : null;
                    return ItemMapper.toResponseItemDto(i, lastBooking, nextBooking);
                }).toList();

        log.info("Loaded {} items for ownerId={}", result.size(), ownerId);
        return result;
    }

    @Override
    public ResponseItemDto getItemById(Long itemId) {
        Item item = getItemByIdWithFetchedCommentOrThrow(itemId);
        log.info("Found item id={}, comments={}", itemId, item.getComments().size());
        return ItemMapper.toResponseItemDto(item);
    }

    @Override
    public ItemDto createItem(Long ownerId, ItemDto itemDto) {
        User owner = getUserByIdOrThrow(ownerId);
        ItemRequest itemRequest = null;
        if (itemDto.getRequest() != null) {
            itemRequest = getItemRequestByIdOrThrowNotFound(itemDto.getRequest());
        }
        Item item = ItemMapper.toItem(
                itemDto,
                owner,
                itemRequest
        );
        Item saved = itemRepository.save(item);
        log.info("Created item id={} for ownerId={}", saved.getId(), ownerId);
        return ItemMapper.toItemDto(saved);
    }

    @Override
    public ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto) {
        Item item = getItemByIdOrThrow(itemId);
        if (!item.getOwner().getId().equals(ownerId)) {
            log.warn("User id={} tried to update item id={} owned by id={}",
                    ownerId, itemId, item.getOwner().getId());
            throw new ForbiddenException("Редактировать вещь может только её владелец");
        }
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        Item updated = itemRepository.save(item);
        log.info("Updated item id={} by ownerId={}", itemId, ownerId);
        return ItemMapper.toItemDto(updated);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            log.info("Search items skipped: blank text");
            return List.of();
        }
        List<ItemDto> items = itemRepository.findAllByAvailableAndText(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
        log.info("Search items text='{}', found={}", text, items.size());
        return items;
    }

    @Override
    public ResponseCommentDto createComment(Long authorId, Long itemId, CommentDto commentDto) {
        LocalDateTime now = LocalDateTime.now();
        User author = getUserByIdOrThrow(authorId);
        Item item = getItemByIdOrThrow(itemId);
        Booking booking = bookingRepository.findByItemIdAndBookerId(itemId, authorId);

        if (booking == null) {
            log.warn("Comment denied: no booking for authorId={}, itemId={}", authorId, itemId);
            throw new BadRequestException("Пользователь не бронировал эту вещь");
        }

        if (
                booking.getStatus() == BookingStatus.APPROVED
                && booking.getEnd().isAfter(now)
        ) {
            log.warn("Comment denied: active approved booking id={} for authorId={}, itemId={}. Now={}, end={}",
                    booking.getId(), authorId, itemId, now, booking.getEnd());
            throw new BadRequestException("Нельзя комментировать при одобренном букинге, который еще не завершен.");
        }

        Comment comment = CommentMapper.toComment(commentDto, author, item);
        Comment saved = commentRepository.save(comment);
        log.info("Created comment id={} for itemId={} by authorId={}", saved.getId(), itemId, authorId);
        return CommentMapper.toResponseCommentDto(saved);
    }

    private User getUserByIdOrThrow(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found: id={}", userId);
                    return new NotFoundException("Пользователь с id=" + userId + " не найден");
                });
    }

    private Item getItemByIdOrThrow(long itemId) {
        return itemRepository
                .findById(itemId)
                .orElseThrow(() -> {
                    log.warn("Item not found: id={}", itemId);
                    return new NotFoundException("Вещь с id=" + itemId + " не найдена");
                });
    }

    private Item getItemByIdWithFetchedCommentOrThrow(long itemId) {
        return itemRepository
                .findByIdWithFetchedComments(itemId)
                .orElseThrow(() -> {
                    log.warn("Item not found: id={}", itemId);
                    return new NotFoundException("Вещь с id=" + itemId + " не найдена");
                });
    }

    private ItemRequest getItemRequestByIdOrThrowNotFound(long requestId) {
        return itemRequestRepository
                .findById(requestId)
                .orElseThrow(() -> {
                    log.warn("ItemRequest not found: id={}", requestId);
                    return new NotFoundException("Запрос с id=" + requestId + " не найден");
                });
    }

    private HashMap<Long, List<Booking>> attachBookingsToItem(List<Booking> bookings) {
        if (bookings == null) {
            return null;
        }
        HashMap<Long, List<Booking>> bookingsByItems = new HashMap<>();
        for (Booking b : bookings) {
            Long itemId = b.getItem().getId();
            bookingsByItems.computeIfAbsent(itemId, id -> new ArrayList<>()).add(b);
        }
        return bookingsByItems;
    }
}
