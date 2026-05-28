package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    List<BookingDto> getBookings(Long userId, BookingState state);

    BookingDto getBookingById(Long bookingId, Long userId);

    BookingDto create(Long requestorId, BookingDto bookingCreationDto);

    BookingDto updateStatus(Long userId, Long bookingId, Boolean approved);

    List<BookingDto> getBookingsOfCurrentUser(Long userId, BookingState state);
}
