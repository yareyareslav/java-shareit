package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

public class ItemRequestConstantsTest {
    public static final String REQUEST_DESCRIPTION = "Нужна дрель";
    public static final ItemRequestDto VALID_ITEM_REQUEST_DTO =
            new ItemRequestDto(null, REQUEST_DESCRIPTION);
    public static final ItemRequestDto INVALID_ITEM_REQUEST_DTO =
            new ItemRequestDto(null, "   ");
    public static final long REQUEST_ID = 1L;
}
