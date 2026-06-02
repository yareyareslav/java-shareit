package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    @DisplayName("Find items by owner id")
    void findAllByOwnerId_ownerHasItems_returnOwnerItems() {
        itemRepository.save(ItemConstantsTest.createItem(
                null, "Дрель", "Описание", true, ItemConstantsTest.OWNER
        ));
        itemRepository.save(ItemConstantsTest.createItem(
                null, "Книга", "Описание", true, ItemConstantsTest.OWNER
        ));

        List<Item> items = itemRepository.findAllByOwnerId(ItemConstantsTest.OWNER.getId());

        assertEquals(2, items.size());
    }

    @Test
    @DisplayName("Search available items by text")
    void findAvailableByText_matchingAvailableItem_returnItem() {
        itemRepository.save(ItemConstantsTest.createItem(
                null, "Дрель", "Описание дрели", true, ItemConstantsTest.OWNER
        ));
        itemRepository.save(ItemConstantsTest.createItem(
                null, "Книга", "Описание книги", false, ItemConstantsTest.OWNER
        ));

        List<Item> items = itemRepository.findAllByAvailableAndText(ItemConstantsTest.SEARCH_TEXT);

        assertEquals(1, items.size());
        assertEquals("Дрель", items.getFirst().getName());
    }
}
