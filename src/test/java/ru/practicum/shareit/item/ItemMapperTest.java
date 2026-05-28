package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemMapperTest {

    @Test
    @DisplayName("Map item to item dto")
    void toItemDto_mapItem_returnItemDto() {
        Item item = ItemConstantsTest.createItem(
                1L, "Дрель", "Описание", true, ItemConstantsTest.OWNER
        );

        ItemDto dto = ItemMapper.toItemDto(item);

        assertEquals(1L, dto.getId());
        assertEquals("Дрель", dto.getName());
        assertEquals("Описание", dto.getDescription());
        assertEquals(true, dto.getAvailable());
        assertEquals(1L, dto.getRequest());
    }
}
