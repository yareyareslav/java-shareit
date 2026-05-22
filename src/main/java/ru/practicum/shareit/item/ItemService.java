package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItemsByOwner(Long ownerId);

    ItemDto getItemById(Long itemId);

    ItemDto createItem(Long ownerId, ItemDto itemDto);

    ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto);

    List<ItemDto> searchItems(String text);
}
