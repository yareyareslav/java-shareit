package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class BookingConstantsTest {
    public static final LocalDateTime BOOKING_START = LocalDateTime.now(ZoneOffset.UTC).plusDays(1);
    public static final LocalDateTime BOOKING_END = BOOKING_START.plusHours(2);
    public static final long ITEM_ID = 1L;
    public static final long BOOKING_ID = 1L;

    public static final BookItemRequestDto VALID_BOOKING_DTO =
            new BookItemRequestDto(ITEM_ID, BOOKING_START, BOOKING_END);

    public static BookItemRequestDto invalidEndBeforeStart() {
        return new BookItemRequestDto(ITEM_ID, BOOKING_START, BOOKING_START.minusHours(1));
    }
}
