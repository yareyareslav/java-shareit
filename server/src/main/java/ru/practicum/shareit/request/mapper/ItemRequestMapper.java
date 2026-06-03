package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequest toEntity(ItemRequestDto requestDto, User requester, List<Item> responses) {
        return new ItemRequest(
                requestDto.getId(),
                requestDto.getDescription(),
                requester,
                LocalDateTime.now(),
                responses != null ?
                        responses :
                        new ArrayList<>()
        );
    }

    public static ResponseItemRequestDto toResponseDto(
            ItemRequest itemRequest
    ) {
        return new ResponseItemRequestDto(
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                itemRequest.getResponses() != null ?
                        itemRequest.getResponses().stream()
                                .map(ItemMapper::toResponseToRequestItemDto)
                                .toList()
                        : null
        );
    }
}
