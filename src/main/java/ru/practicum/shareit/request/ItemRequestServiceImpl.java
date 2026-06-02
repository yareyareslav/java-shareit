package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Slf4j
@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    @Override
    public List<ItemDto> getAllItems() {
        log.info("Item requests: getAllItems (stub)");
        return List.of();
    }

    @Override
    public ItemDto getItemById(Long id) {
        log.info("Item requests: getItemById id={} (stub)", id);
        return null;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto) {
        log.info("Item requests: createItem (stub)");
        return null;
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto) {
        log.info("Item requests: updateItem (stub)");
        return null;
    }
}
