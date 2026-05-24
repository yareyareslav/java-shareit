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
        userRepository.create(ItemConstantsTest.OWNER);
    }

    @Test
    @DisplayName("Create item assigns id")
    void createItem_new_assignsId() {
        Item item = ItemConstantsTest.createItem(
                null,
                ItemConstantsTest.VALID_ITEM_DTO.getName(),
                ItemConstantsTest.VALID_ITEM_DTO.getDescription(),
                ItemConstantsTest.VALID_ITEM_DTO.getAvailable(),
                ItemConstantsTest.OWNER
        );

        Item saved = itemRepository.create(item);

        assertNotNull(saved.getId());
        assertEquals(ItemConstantsTest.VALID_ITEM_DTO.getName(), saved.getName());
    }

    @Test
    @DisplayName("Get item by id")
    void findItemById_existingItem_return() {
        Item saved = itemRepository.create(ItemConstantsTest.createItem(
                null, "Дрель", "Описание", true, ItemConstantsTest.OWNER
        ));

        Optional<Item> item = itemRepository.findById(saved.getId());

        assertTrue(item.isPresent());
        assertEquals("Дрель", item.get().getName());
    }

    @Test
    @DisplayName("Find items by owner id")
    void getByOwnerId_ownerHasItems_returnOwnerItems() {
        itemRepository.create(ItemConstantsTest.createItem(
                null, "Дрель", "Описание", true, ItemConstantsTest.OWNER
        ));
        itemRepository.create(ItemConstantsTest.createItem(
                null, "Книга", "Описание", true, ItemConstantsTest.OWNER
        ));

        List<Item> items = itemRepository.getByOwnerId(ItemConstantsTest.OWNER.getId());

        assertEquals(2, items.size());
    }

    @Test
    @DisplayName("Search available items by text")
    void getAvailableByText_matchingAvailableItem_returnItem() {
        itemRepository.create(ItemConstantsTest.createItem(
                null, "Дрель", "Описание дрели", true, ItemConstantsTest.OWNER
        ));
        itemRepository.create(ItemConstantsTest.createItem(
                null, "Книга", "Описание книги", false, ItemConstantsTest.OWNER
        ));

        List<Item> items = itemRepository.getAvailableByText(ItemConstantsTest.SEARCH_TEXT);

        assertEquals(1, items.size());
        assertEquals("Дрель", items.getFirst().getName());
    }

    @Test
    @DisplayName("Update item")
    void updateItem_existingItem_returnUpdated() {
        Item saved = itemRepository.create(ItemConstantsTest.createItem(
                null, "Дрель", "Описание", true, ItemConstantsTest.OWNER
        ));
        saved.setName("Отвёртка");

        Item updated = itemRepository.update(saved);

        assertEquals("Отвёртка", updated.getName());
        assertEquals("Отвёртка", itemRepository.findById(saved.getId()).orElseThrow().getName());
    }
}
