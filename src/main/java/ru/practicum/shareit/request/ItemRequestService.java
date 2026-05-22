package ru.practicum.shareit.request;


import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemRequestService {
    List<ItemDto> getAllItems();

    ItemDto getItemById(Long id);

    ItemDto createItem(ItemDto itemDto);

    ItemDto updateItem(ItemDto itemDto);
}
