package ru.practicum.shareit.item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Optional<Item> getItemById(Long id);

    List<Item> findByOwnerId(Long ownerId);

    List<Item> searchAvailableByText(String text);

    Item createItem(Item item);

    Item updateItem(Item item);
}
