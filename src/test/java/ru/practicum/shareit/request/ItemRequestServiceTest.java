package ru.practicum.shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ItemRequestServiceTest {

    @Autowired
    ItemRequestService itemRequestService;

    @Test
    @DisplayName("Get all items returns empty list")
    void getAllItems_returnEmptyList() {
        assertTrue(itemRequestService.getAllItems().isEmpty());
    }

    @Test
    @DisplayName("Get item by id returns null")
    void getItemById_returnNull() {
        assertNull(itemRequestService.getItemById(1L));
    }

    @Test
    @DisplayName("Create item returns null")
    void createItem_returnNull() {
        assertNull(itemRequestService.createItem(null));
    }

    @Test
    @DisplayName("Update item returns null")
    void updateItem_returnNull() {
        assertNull(itemRequestService.updateItem(null));
    }
}
