package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserConstantsTest;

public class ItemConstantsTest {
    public static final ItemDto VALID_ITEM_DTO =
            new ItemDto(null, "Дрель", "Описание дрели", true, null);
    public static final ItemDto UNAVAILABLE_ITEM_DTO =
            new ItemDto(null, "Книга", "Описание книги", false, null);
    public static final ItemDto ITEM_UPDATE_DTO =
            new ItemDto(null, "Отвёртка", "Новое описание", false, null);
    public static final String SEARCH_TEXT = "дрел";

    public static final User OWNER = UserConstantsTest.VALID_USER_1;

    public static Item createItem(Long id, String name, String description, boolean available, User owner) {
        return new Item(id, name, description, available, owner, null);
    }
}
