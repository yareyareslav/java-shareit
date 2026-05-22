package ru.practicum.shareit.request;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    @Override
    public List<ItemDto> getAllItems() {
        return List.of();
    }

    @Override
    public ItemDto getItemById(Long id) {
        return null;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto) {
        return null;
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto) {
        return null;
    }
}
