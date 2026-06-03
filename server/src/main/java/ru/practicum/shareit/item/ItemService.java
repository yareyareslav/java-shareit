package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;

import java.util.List;

public interface ItemService {
    List<ResponseItemDto> getItemsByOwner(Long ownerId);

    ResponseItemDto getItemById(Long itemId);

    ItemDto createItem(Long ownerId, ItemDto itemDto);

    ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto);

    List<ItemDto> searchItems(String text);

    ResponseCommentDto createComment(Long authorId, Long itemId, CommentDto commentDto);
}
