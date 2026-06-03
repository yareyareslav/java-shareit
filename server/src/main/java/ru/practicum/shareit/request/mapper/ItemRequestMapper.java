package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.dto.ResponseToItemRequestDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequest toEntity(ItemRequestDto requestDto, User requester) {
        return new ItemRequest(
                null,
                requestDto.getDescription(),
                requester,
                LocalDateTime.now()
        );
    }

    public static ResponseItemRequestDto toResponseDto(
            ItemRequest itemRequest,
            List<ResponseToItemRequestDto> responses
    ) {
        return new ResponseItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                responses != null ?
                        responses :
                        List.of()
        );
    }
}
