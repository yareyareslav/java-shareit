package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long nextId = 1;

    @Override
    public Optional<Item> getItemById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> findByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .toList();
    }

    @Override
    public List<Item> searchAvailableByText(String text) {
        String searchText = text.toLowerCase();
        return items.values().stream()
                .filter(Item::isAvailable)
                .filter(item -> containsText(item.getName(), searchText)
                        || containsText(item.getDescription(), searchText))
                .toList();
    }

    @Override
    public Item createItem(Item item) {
        if (item.getId() == null) {
            item.setId(nextId++);
        }
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    private boolean containsText(String value, String searchText) {
        return value != null && value.toLowerCase().contains(searchText);
    }
}
