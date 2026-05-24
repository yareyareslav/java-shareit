package ru.practicum.shareit.booking.mapper;


import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.shared.mapper.TwoWayMapper;

public class BookingMapper implements TwoWayMapper<Booking, BookingDto> {
    @Override
    public Booking toData(BookingDto bookingDto) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStartRentDate() != null
                        ? bookingDto.getStartRentDate().atStartOfDay()
                        : null,
                bookingDto.getEndRentDate() != null
                        ? bookingDto.getEndRentDate().atStartOfDay()
                        : null,
                null,
                null,
                bookingDto.getStatus()
        );
    }

    @Override
    public BookingDto toPresentation(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart() != null ? booking.getStart().toLocalDate() : null,
                booking.getEnd() != null ? booking.getEnd().toLocalDate() : null,
                booking.getItem() != null ? booking.getItem().getId() : null,
                booking.getBooker() != null ? booking.getBooker().getId() : null,
                booking.getStatus()
        );
    }
}
