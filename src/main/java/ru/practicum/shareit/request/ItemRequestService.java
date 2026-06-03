package ru.practicum.shareit.request;


import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    List<ResponseItemRequestDto> getOwnRequests(long userId);

    List<ResponseItemRequestDto> getOthersRequests(long userId);

    ResponseItemRequestDto getRequestById(Long id);

    ResponseItemRequestDto addRequest(long userId, ItemRequestDto requestDto);
}
