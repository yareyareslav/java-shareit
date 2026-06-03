package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserConstantsTest;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ItemRequestConstantsTest {
    public static final Long DEFAULT_REQUEST_ID = 1L;
    public static final Long NON_EXISTING_REQUEST_ID = 999L;
    public static final String REQUEST_DESCRIPTION = "Нужна дрель";
    public static final String OTHER_REQUEST_DESCRIPTION = "Нужна книга";
    public static final LocalDateTime REQUEST_CREATED = LocalDateTime.of(2024, 1, 15, 10, 0);

    public static final User REQUESTER = UserConstantsTest.VALID_USER_1;
    public static final User OTHER_REQUESTER = UserConstantsTest.VALID_USER_2;

    public static final ItemRequestDto VALID_ITEM_REQUEST_DTO =
            new ItemRequestDto(null, REQUEST_DESCRIPTION);

    public static ItemRequest createItemRequest(
            Long id,
            String description,
            User requester,
            LocalDateTime created
    ) {
        return new ItemRequest(
                id,
                description,
                requester,
                created,
                new ArrayList<>()
        );
    }

    public static ItemRequest defaultRequest() {
        return createItemRequest(
                DEFAULT_REQUEST_ID,
                REQUEST_DESCRIPTION,
                REQUESTER,
                REQUEST_CREATED
        );
    }

    public static ItemRequest otherUserRequest() {
        return createItemRequest(
                2L,
                OTHER_REQUEST_DESCRIPTION,
                OTHER_REQUESTER,
                REQUEST_CREATED.plusHours(1)
        );
    }
}
