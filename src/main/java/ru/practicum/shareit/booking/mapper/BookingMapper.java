package ru.practicum.shareit.booking.mapper;


import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

public class BookingMapper {
    public static Booking toData(BookingDto bookingDto, Item item, User booker) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStart() != null
                        ? bookingDto.getStart()
                        : null,
                bookingDto.getEnd() != null
                        ? bookingDto.getEnd()
                        : null,
                item,
                booker,
                bookingDto.getStatus()
        );
    }

    public static BookingDto toPresentation(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem() != null ? booking.getItem().getId() : null,
                booking.getBooker() != null ? booking.getBooker().getId() : null,
                booking.getStatus()
        );
    }

    public static ResponseBookingDto toResponseDto(Booking booking) {
        return new ResponseBookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus()
        );
    }
}
