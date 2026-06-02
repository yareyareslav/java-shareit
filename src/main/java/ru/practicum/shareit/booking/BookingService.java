package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;

import java.util.List;

public interface BookingService {
    List<ResponseBookingDto> getBookings(Long userId, BookingState state);

    ResponseBookingDto getBookingById(Long bookingId, Long userId);

    ResponseBookingDto create(Long requestorId, BookingDto bookingCreationDto);

    ResponseBookingDto updateStatus(Long userId, Long bookingId, Boolean approved);

    List<ResponseBookingDto> getBookingsOfItemsOwnedByUser(Long userId, BookingState state);
}
