package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

public class ItemConstantsTest {
    public static final ItemDto VALID_ITEM_DTO =
            new ItemDto(null, "Дрель", "Описание дрели", true, null, null);
    public static final ItemDto INVALID_ITEM_DTO =
            new ItemDto(null, "", "Описание", true, null, null);
    public static final ItemDto ITEM_UPDATE_DTO =
            new ItemDto(null, "Отвёртка", "Новое описание", false, null, null);
    public static final CommentDto VALID_COMMENT_DTO = new CommentDto(null, null, "Отличная вещь");
    public static final String SEARCH_TEXT = "дрел";
}
