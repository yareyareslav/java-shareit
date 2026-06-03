package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemConstantsTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BookingMapperTest {

    @Test
    @DisplayName("Map booking dto to booking entity")
    void toData_mapDto_returnBooking() {
        Item item = ItemConstantsTest.createItem(1L, "Дрель", "Описание", true, ItemConstantsTest.OWNER);

        Booking booking = BookingMapper.toData(
                BookingConstantsTest.VALID_BOOKING_DTO, item, BookingConstantsTest.BOOKER);

        assertEquals(BookingConstantsTest.FUTURE_START, booking.getStart());
        assertEquals(BookingConstantsTest.FUTURE_END, booking.getEnd());
        assertEquals(item, booking.getItem());
        assertEquals(BookingConstantsTest.BOOKER, booking.getBooker());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }

    @Test
    @DisplayName("Map booking dto with null dates to booking entity")
    void toData_nullDates_returnBookingWithNullDates() {
        Item item = ItemConstantsTest.createItem(1L, "Дрель", "Описание", true, ItemConstantsTest.OWNER);
        BookingDto dto = new BookingDto(1L, null, null, 1L, 2L, BookingStatus.WAITING);

        Booking booking = BookingMapper.toData(dto, item, BookingConstantsTest.BOOKER);

        assertNull(booking.getStart());
        assertNull(booking.getEnd());
    }

    @Test
    @DisplayName("Map booking to presentation dto")
    void toPresentation_mapBooking_returnDto() {
        Item item = ItemConstantsTest.createItem(1L, "Дрель", "Описание", true, ItemConstantsTest.OWNER);
        Booking booking = BookingConstantsTest.createLastApprovedBooking(item);

        BookingDto dto = BookingMapper.toPresentation(booking);

        assertEquals(booking.getId(), dto.getId());
        assertEquals(item.getId(), dto.getItemId());
        assertEquals(BookingConstantsTest.BOOKER.getId(), dto.getBookerId());
        assertEquals(BookingStatus.APPROVED, dto.getStatus());
    }

    @Test
    @DisplayName("Map booking without relations to presentation dto")
    void toPresentation_bookingWithoutRelations_returnDtoWithNullIds() {
        Booking booking = new Booking(1L, null, null, null, null, BookingStatus.WAITING);

        BookingDto dto = BookingMapper.toPresentation(booking);

        assertNull(dto.getItemId());
        assertNull(dto.getBookerId());
    }

    @Test
    @DisplayName("Map booking to response dto")
    void toResponseDto_mapBooking_returnResponseDto() {
        Item item = ItemConstantsTest.createItem(1L, "Дрель", "Описание", true, ItemConstantsTest.OWNER);
        Booking booking = BookingConstantsTest.createLastApprovedBooking(item);

        ResponseBookingDto dto = BookingMapper.toResponseDto(booking);

        assertEquals(booking.getId(), dto.getId());
        assertEquals(item, dto.getItem());
        assertEquals(BookingConstantsTest.BOOKER, dto.getBooker());
        assertEquals(BookingStatus.APPROVED, dto.getStatus());
    }
}
