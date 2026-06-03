package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.shared.constant.Headers;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping
    public List<ResponseBookingDto> getBookings(@RequestHeader(Headers.USER_ID) Long userId,
                                                @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("GET /bookings userId={}, state={}", userId, state);
        return bookingService.getBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<ResponseBookingDto> getBookingsOfItemsOwnedByUser(@RequestHeader(Headers.USER_ID) Long userId,
                                                                   @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("GET /bookings/owner userId={}, state={}", userId, state);
        return bookingService.getBookingsOfItemsOwnedByUser(userId, state);
    }

    @GetMapping("/{bookingId}")
    public ResponseBookingDto getBookingById(@RequestHeader(Headers.USER_ID) Long userId,
                                             @PathVariable Long bookingId) {
        log.info("GET /bookings/{} userId={}", bookingId, userId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @PostMapping
    public ResponseBookingDto createBooking(@RequestHeader(Headers.USER_ID) Long requestorId,
                                            @RequestBody BookingDto bookingDto) {
        log.info("POST /bookings requestorId={}, itemId={}", requestorId, bookingDto.getItemId());
        return bookingService.create(requestorId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseBookingDto updateStatus(@RequestHeader(Headers.USER_ID) Long userId,
                                           @PathVariable Long bookingId,
                                           @RequestParam(defaultValue = "true") Boolean approved) {
        log.info("PATCH /bookings/{} userId={}, approved={}", bookingId, userId, approved);
        return bookingService.updateStatus(userId, bookingId, approved);
    }
}
