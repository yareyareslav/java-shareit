package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.shared.error.ForbiddenException;
import ru.practicum.shareit.shared.error.NotFoundException;
import ru.practicum.shareit.user.UserConstantsTest;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    ItemServiceImpl itemService;

    @Test
    @DisplayName("Get items by owner")
    void getItemsByOwner_existingOwner_returnItems() {
        Item item = ItemConstantsTest.createItem(1L, "Дрель", "Описание", true, ItemConstantsTest.OWNER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(ItemConstantsTest.OWNER));
        when(itemRepository.findByOwnerId(1L)).thenReturn(List.of(item));

        List<ItemDto> items = itemService.getItemsByOwner(1L);

        assertEquals(1, items.size());
        assertEquals("Дрель", items.getFirst().getName());
    }

    @Test
    @DisplayName("Get items by non-existing owner")
    void getItemsByOwner_nonExistingOwner_throwNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemsByOwner(99L));
    }

    @Test
    @DisplayName("Get item by id")
    void getItemById_existingItem_returnItemDto() {
        Item item = ItemConstantsTest.createItem(1L, "Дрель", "Описание", true, ItemConstantsTest.OWNER);
        when(itemRepository.getItemById(1L)).thenReturn(Optional.of(item));

        ItemDto dto = itemService.getItemById(1L);

        assertEquals("Дрель", dto.getName());
    }

    @Test
    @DisplayName("Get non-existing item by id")
    void getItemById_nonExistingItem_throwNotFoundException() {
        when(itemRepository.getItemById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemById(99L));
    }

    @Test
    @DisplayName("Create item")
    void createItem_validOwner_returnItemDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(ItemConstantsTest.OWNER));
        when(itemRepository.createItem(any(Item.class))).thenAnswer(invocation -> {
            Item item = invocation.getArgument(0);
            item.setId(10L);
            return item;
        });

        ItemDto dto = itemService.createItem(1L, ItemConstantsTest.VALID_ITEM_DTO);

        assertEquals(10L, dto.getId());
        assertEquals("Дрель", dto.getName());
    }

    @Test
    @DisplayName("Update item by owner")
    void updateItem_ownerUpdatesOwnItem_returnUpdatedItemDto() {
        Item item = ItemConstantsTest.createItem(1L, "Дрель", "Описание", true, ItemConstantsTest.OWNER);
        when(itemRepository.getItemById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.updateItem(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemDto dto = itemService.updateItem(1L, 1L, ItemConstantsTest.ITEM_UPDATE_DTO);

        assertEquals("Отвёртка", dto.getName());
        assertEquals(false, dto.getAvailable());
    }

    @Test
    @DisplayName("Update item by non-owner")
    void updateItem_nonOwner_throwForbiddenException() {
        Item item = ItemConstantsTest.createItem(
                1L, "Дрель", "Описание", true, UserConstantsTest.VALID_USER_2
        );
        when(itemRepository.getItemById(1L)).thenReturn(Optional.of(item));

        assertThrows(ForbiddenException.class,
                () -> itemService.updateItem(1L, 1L, ItemConstantsTest.ITEM_UPDATE_DTO));
        verify(itemRepository, never()).updateItem(any());
    }

    @Test
    @DisplayName("Search items with blank text")
    void searchItems_blankText_returnEmptyList() {
        List<ItemDto> items = itemService.searchItems("   ");

        assertTrue(items.isEmpty());
    }

    @Test
    @DisplayName("Search items by text")
    void searchItems_matchingText_returnItems() {
        Item item = ItemConstantsTest.createItem(1L, "Дрель", "Описание", true, ItemConstantsTest.OWNER);
        when(itemRepository.searchAvailableByText(ItemConstantsTest.SEARCH_TEXT)).thenReturn(List.of(item));

        List<ItemDto> items = itemService.searchItems(ItemConstantsTest.SEARCH_TEXT);

        assertEquals(1, items.size());
    }
}
