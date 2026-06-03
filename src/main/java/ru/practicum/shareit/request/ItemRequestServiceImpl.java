package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.shared.error.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public List<ResponseItemRequestDto> getOwnRequests(long userId) {
        getUserByIdOrThrowNotFound(userId);
        List<ItemRequest> requestsOfUser = itemRequestRepository
                .findAllByRequesterId(userId);
        return requestsOfUser.stream()
                .map(ItemRequestMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<ResponseItemRequestDto> getOthersRequests(long userId) {
        getUserByIdOrThrowNotFound(userId);
        List<ItemRequest> requestsOfOthers = itemRequestRepository
                .findAllRequestsOfOthers(userId);
        return requestsOfOthers.stream()
                .map(ItemRequestMapper::toResponseDto)
                .toList();
    }

    @Override
    public ResponseItemRequestDto getRequestById(Long id) {
        ItemRequest request = getRequestByIdOrThrowNotFound(id);
        return ItemRequestMapper.toResponseDto(request);
    }

    @Override
    public ResponseItemRequestDto addRequest(long userId, ItemRequestDto requestDto) {
        User user = getUserByIdOrThrowNotFound(userId);
        ItemRequest itemRequest = ItemRequestMapper.toEntity(requestDto, user, null);
        return ItemRequestMapper.toResponseDto(itemRequestRepository.save(itemRequest));
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
