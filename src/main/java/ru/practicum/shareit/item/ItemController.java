package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.shared.constant.Headers;
import ru.practicum.shareit.shared.dto.group.OnCreate;
import ru.practicum.shareit.shared.error.BadRequestException;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(
            @RequestHeader(Headers.USER_ID) Long userId,
            @Validated(OnCreate.class) @RequestBody ItemDto itemDto
    ) {
        if (userId == null) {
            throw new BadRequestException(Headers.USER_ID + " не должен быть пустым");
        }
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @PathVariable Long itemId,
            @RequestHeader(Headers.USER_ID) Long userId,
            @RequestBody ItemDto itemDto
    ) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseCommentDto createComment(
            @PathVariable Long itemId,
            @RequestHeader(Headers.USER_ID) Long userId,
            @Validated(OnCreate.class) @RequestBody CommentDto commentDto
    ) {
        return itemService.createComment(userId, itemId, commentDto);
    }

    @GetMapping("/{itemId}")
    public ResponseItemDto getItem(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ResponseItemDto> getOwnerItems(@RequestHeader(Headers.USER_ID) Long userId) {
        return itemService.getItemsByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }
}