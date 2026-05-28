package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserConstantsTest;

import java.time.LocalDateTime;

public class BookingConstantsTest {
    public static final Long DEFAULT_ITEM_ID = 1L;
    public static final Long LAST_BOOKING_ID = 10L;
    public static final Long NEXT_BOOKING_ID = 11L;
    public static final Long NON_EXISTING_BOOKING_ID = 999L;

    public static final LocalDateTime FUTURE_START = LocalDateTime.now().plusDays(1);
    public static final LocalDateTime FUTURE_END = LocalDateTime.now().plusDays(2);
    public static final LocalDateTime PAST_START = LocalDateTime.now().minusDays(5);
    public static final LocalDateTime PAST_END = LocalDateTime.now().minusDays(3);

    public static final User BOOKER = UserConstantsTest.VALID_USER_2;

    public static final BookingDto VALID_BOOKING_DTO = new BookingDto(
            null,
            FUTURE_START,
            FUTURE_END,
            DEFAULT_ITEM_ID,
            null,
            BookingStatus.WAITING
    );

    public static final BookingDto APPROVED_BOOKING_DTO = new BookingDto(
            LAST_BOOKING_ID,
            PAST_START,
            PAST_END,
            DEFAULT_ITEM_ID,
            BOOKER.getId(),
            BookingStatus.APPROVED
    );

    public static Booking createBooking(
            Long id,
            LocalDateTime start,
            LocalDateTime end,
            Item item,
            User booker,
            BookingStatus status
    ) {
        return new Booking(id, start, end, item, booker, status);
    }

    public static Booking createLastApprovedBooking(Item item) {
        return createBooking(LAST_BOOKING_ID, PAST_START, PAST_END, item, BOOKER, BookingStatus.APPROVED);
    }

    public static Booking createNextApprovedBooking(Item item) {
        return createBooking(NEXT_BOOKING_ID, FUTURE_START, FUTURE_END, item, BOOKER, BookingStatus.APPROVED);
    }
}
