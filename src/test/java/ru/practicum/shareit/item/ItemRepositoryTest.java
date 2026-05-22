package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.save(ItemConstantsTest.OWNER);
    }

    @Test
    @DisplayName("Create item assigns id")
    void createItem_newItem_assignsId() {
        Item item = ItemConstantsTest.createItem(
                null,
                ItemConstantsTest.VALID_ITEM_DTO.getName(),
                ItemConstantsTest.VALID_ITEM_DTO.getDescription(),
                ItemConstantsTest.VALID_ITEM_DTO.getAvailable(),
                ItemConstantsTest.OWNER
        );

        Item saved = itemRepository.createItem(item);

        assertNotNull(saved.getId());
        assertEquals(ItemConstantsTest.VALID_ITEM_DTO.getName(), saved.getName());
    }

    @Test
    @DisplayName("Get item by id")
    void getItemById_existingItem_returnItem() {
        Item saved = itemRepository.createItem(ItemConstantsTest.createItem(
                null, "Дрель", "Описание", true, ItemConstantsTest.OWNER
        ));

        Optional<Item> item = itemRepository.getItemById(saved.getId());

        assertTrue(item.isPresent());
        assertEquals("Дрель", item.get().getName());
    }

    @Test
    @DisplayName("Find items by owner id")
    void findByOwnerId_ownerHasItems_returnOwnerItems() {
        itemRepository.createItem(ItemConstantsTest.createItem(
                null, "Дрель", "Описание", true, ItemConstantsTest.OWNER
        ));
        itemRepository.createItem(ItemConstantsTest.createItem(
                null, "Книга", "Описание", true, ItemConstantsTest.OWNER
        ));

        List<Item> items = itemRepository.findByOwnerId(ItemConstantsTest.OWNER.getId());

        assertEquals(2, items.size());
    }

    @Test
    @DisplayName("Search available items by text")
    void searchAvailableByText_matchingAvailableItem_returnItem() {
        itemRepository.createItem(ItemConstantsTest.createItem(
                null, "Дрель", "Описание дрели", true, ItemConstantsTest.OWNER
        ));
        itemRepository.createItem(ItemConstantsTest.createItem(
                null, "Книга", "Описание книги", false, ItemConstantsTest.OWNER
        ));

        List<Item> items = itemRepository.searchAvailableByText(ItemConstantsTest.SEARCH_TEXT);

        assertEquals(1, items.size());
        assertEquals("Дрель", items.getFirst().getName());
    }

    @Test
    @DisplayName("Update item")
    void updateItem_existingItem_returnUpdatedItem() {
        Item saved = itemRepository.createItem(ItemConstantsTest.createItem(
                null, "Дрель", "Описание", true, ItemConstantsTest.OWNER
        ));
        saved.setName("Отвёртка");

        Item updated = itemRepository.updateItem(saved);

        assertEquals("Отвёртка", updated.getName());
        assertEquals("Отвёртка", itemRepository.getItemById(saved.getId()).orElseThrow().getName());
    }
}
