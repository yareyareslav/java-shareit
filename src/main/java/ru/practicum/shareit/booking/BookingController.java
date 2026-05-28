package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.shared.constant.Headers;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping
    public List<BookingDto> getBookings(@RequestHeader(Headers.USER_ID) Long userId,
                                        @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsOfCurrentUser(@RequestHeader(Headers.USER_ID) Long userId,
                                                      @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getBookingsOfCurrentUser(userId, state);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader(Headers.USER_ID) Long userId,
                                     @PathVariable Long bookingId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @PostMapping
    public BookingDto createBooking(@RequestHeader(Headers.USER_ID) Long requestorId,
                                    @Validated @RequestBody BookingDto bookingDto) {
        return bookingService.create(requestorId, bookingDto);
    }

    @PatchMapping("/{id}")
    public BookingDto updateStatus(@RequestHeader(Headers.USER_ID) Long userId,
                                   @PathVariable Long bookingId,
                                   @RequestParam(defaultValue = "true") Boolean approved) {
        return bookingService.updateStatus(userId, bookingId, approved);
    }
}
