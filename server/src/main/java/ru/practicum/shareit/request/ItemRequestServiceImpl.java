package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ResponseToItemRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.shared.error.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public List<ResponseItemRequestDto> getOwnRequests(long userId) {
        getUserByIdOrThrowNotFound(userId);
        List<ItemRequest> requestsOfUser = itemRequestRepository
                .findAllByRequesterId(userId);
        return attachResponsesToRequests(requestsOfUser);
    }

    @Override
    public List<ResponseItemRequestDto> getOthersRequests(long userId) {
        getUserByIdOrThrowNotFound(userId);
        List<ItemRequest> requestsOfOthers = itemRequestRepository
                .findAllRequestsOfOthers(userId);
        return attachResponsesToRequests(requestsOfOthers);
    }

    @Override
    public ResponseItemRequestDto getRequestById(Long id) {
        ItemRequest request = getRequestByIdOrThrowNotFound(id);
        List<ResponseToItemRequestDto> responses = itemRepository.findAllByRequestId(id)
                .stream()
                .map(ItemMapper::toResponseToRequestItemDto)
                .toList();
        return ItemRequestMapper.toResponseDto(request, responses);
    }

    @Override
    public ResponseItemRequestDto addRequest(long userId, ItemRequestDto requestDto) {
        User user = getUserByIdOrThrowNotFound(userId);
        ItemRequest itemRequest = ItemRequestMapper.toEntity(requestDto, user);
        return ItemRequestMapper.toResponseDto(itemRequestRepository.save(itemRequest), List.of());
    }

    private List<ResponseItemRequestDto> attachResponsesToRequests(List<ItemRequest> itemRequests) {
        if (itemRequests.isEmpty()) {
            return List.of();
        }
        List<Long> requestIds = itemRequests.stream()
                .map(ItemRequest::getId)
                .toList();

        List<Item> items = itemRepository.findAllByRequestIds(requestIds);
        HashMap<Long, List<ResponseToItemRequestDto>> itemsByRequestId = new HashMap<>();
        for (Item item : items) {
            itemsByRequestId
                    .computeIfAbsent(item.getRequest().getId(), id -> new ArrayList<>())
                    .add(ItemMapper.toResponseToRequestItemDto(item));
        }
        return itemRequests.stream()
                .map(req ->
                        ItemRequestMapper
                                .toResponseDto(req, itemsByRequestId.getOrDefault(req.getId(), List.of()))
                )
                .toList();
    }

    private Item getItemByIdOrThrowNotFound(long itemId) {
        return itemRepository
                .findById(itemId)
                .orElseThrow(() -> {
                    log.warn("Item not found: id={}", itemId);
                    return new NotFoundException("Предмет с id=" + itemId + " не найден");
                });
    }

    private ItemRequest getRequestByIdOrThrowNotFound(long requestId) {
        return itemRequestRepository
                .findById(requestId)
                .orElseThrow(() -> {
                    log.warn("ItemRequest not found: id={}", requestId);
                    return new NotFoundException("Запрос с id=" + requestId + " не найден");
                });
    }

    private User getUserByIdOrThrowNotFound(long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found: id={}", userId);
                    return new NotFoundException("Пользователь с id=" + userId + " не найден");
                });
    }
}
