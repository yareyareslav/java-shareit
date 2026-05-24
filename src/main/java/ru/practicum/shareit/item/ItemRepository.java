package ru.practicum.shareit.item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Optional<Item> findById(Long id);

    List<Item> getByOwnerId(Long ownerId);

    List<Item> getAvailableByText(String text);

    Item create(Item item);

    Item update(Item item);
}
