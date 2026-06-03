package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.shared.constant.Headers;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(
            @RequestHeader(Headers.USER_ID) Long userId,
            @RequestBody ItemDto itemDto
    ) {
        log.info("POST /items userId={}, name={}", userId, itemDto.getName());
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @PathVariable Long itemId,
            @RequestHeader(Headers.USER_ID) Long userId,
            @RequestBody ItemDto itemDto
    ) {
        log.info("PATCH /items/{} userId={}", itemId, userId);
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseCommentDto createComment(
            @PathVariable Long itemId,
            @RequestHeader(Headers.USER_ID) Long userId,
            @RequestBody CommentDto commentDto
    ) {
        log.info("POST /items/{}/comment userId={}", itemId, userId);
        return itemService.createComment(userId, itemId, commentDto);
    }

    @GetMapping("/{itemId}")
    public ResponseItemDto getItem(@PathVariable Long itemId) {
        log.info("GET /items/{}", itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ResponseItemDto> getOwnerItems(@RequestHeader(Headers.USER_ID) Long userId) {
        log.info("GET /items userId={}", userId);
        return itemService.getItemsByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("GET /items/search text={}", text);
        return itemService.searchItems(text);
    }
}
