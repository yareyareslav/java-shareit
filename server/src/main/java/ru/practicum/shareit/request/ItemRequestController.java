package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.shared.constant.Headers;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ResponseItemRequestDto> getOwnRequests(@RequestHeader(value = Headers.USER_ID) Long userId) {
        return itemRequestService.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public List<ResponseItemRequestDto> getOthersRequests(@RequestHeader(value = Headers.USER_ID) Long userId) {
        return itemRequestService.getOthersRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseItemRequestDto getRequestById(@PathVariable Long requestId) {
        return itemRequestService.getRequestById(requestId);
    }

    @PostMapping
    public ResponseItemRequestDto addRequest(@RequestHeader(value = Headers.USER_ID) Long userId,
                                             @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.addRequest(userId, itemRequestDto);
    }
}
