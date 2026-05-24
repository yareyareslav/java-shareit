package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.shared.mapper.TwoWayMapper;

public class ItemRequestToItemRequestDtoMapper implements TwoWayMapper<ItemRequest, ItemRequestDto> {
    @Override
    public ItemRequest toData(ItemRequestDto itemRequestDto) {
        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                itemRequestDto.getRequestor(),
                null
        );
    }

    @Override
    public ItemRequestDto toPresentation(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getRequestor(),
                itemRequest.getDescription()
        );
    }
}
