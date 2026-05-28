package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
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
import ru.practicum.shareit.shared.error.BadRequestException;
import ru.practicum.shareit.shared.error.ForbiddenException;
import ru.practicum.shareit.shared.error.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public List<ResponseItemDto> getItemsByOwner(Long ownerId) {
        getUserByIdOrThrow(ownerId);
        List<Item> items = itemRepository.findAllWithFetchedComments(ownerId);
        Booking lastBooking = bookingRepository.findPastApprovedByItemOwnerId(ownerId);
        Booking nextBooking = bookingRepository.findFutureApprovedByItemOwnerId(ownerId);

        return items.stream()
                .map(item -> ItemMapper.toResponseItemDto(
                        item,
                        lastBooking,
                        nextBooking
                ))
                .toList();
    }

    @Override
    public ResponseItemDto getItemById(Long itemId) {
        Item item = getItemByIdWithFetchedCommentOrThrow(itemId);
        return ItemMapper.toResponseItemDto(item);
    }

    @Override
    public ItemDto createItem(Long ownerId, ItemDto itemDto) {
        User owner = getUserByIdOrThrow(ownerId);
        Item item = ItemMapper.toItem(
                itemDto,
                owner,
                null
        );
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto) {
        Item item = getItemByIdOrThrow(itemId);
        if (!item.getOwner().getId().equals(ownerId)) {
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
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.findAllByAvailableAndText(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public ResponseCommentDto createComment(Long authorId, Long itemId, CommentDto commentDto) {
        User author = getUserByIdOrThrow(authorId);
        Item item = getItemByIdOrThrow(itemId);
        Booking booking = bookingRepository.findByItemIdAndBookerId(itemId, authorId);

        if (booking.getStatus() == BookingStatus.APPROVED && booking.getEnd().isAfter(LocalDateTime.now())) {
            throw new BadRequestException("Нельзя комментировать при одобренном букинге, который еще не завершен.");
        }

        Comment comment = CommentMapper.toComment(commentDto, author, item);
        return CommentMapper.toResponseCommentDto(commentRepository.save(comment));
    }

    private User getUserByIdOrThrow(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }

    private Item getItemByIdOrThrow(long itemId) {
        return itemRepository
                .findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена"));
    }

    private Item getItemByIdWithFetchedCommentOrThrow(long itemId) {
        return itemRepository
                .findByIdWithFetchedComments(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена"));
    }
}
