package ru.practicum.shareit.request;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.shared.constant.Headers;
import ru.practicum.shareit.shared.dto.group.OnCreate;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Object> getOwnRequests(@RequestHeader(Headers.USER_ID) long userId) {
        log.info("GET /requests userId={}", userId);
        return itemRequestClient.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOthersRequests(@RequestHeader(Headers.USER_ID) long userId) {
        log.info("GET /requests/all userId={}", userId);
        return itemRequestClient.getOthersRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(Headers.USER_ID) long userId,
                                                 @PathVariable long requestId) {
        log.info("GET /requests/{} userId={}", requestId, userId);
        return itemRequestClient.getRequestById(userId, requestId);
    }

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader(Headers.USER_ID) long userId,
                                             @Validated(OnCreate.class) @RequestBody ItemRequestDto itemRequestDto) {
        log.info("POST /requests userId={}", userId);
        return itemRequestClient.addRequest(userId, itemRequestDto);
    }
}
