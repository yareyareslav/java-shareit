package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startRentDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endRentDate;
    private Long itemId;
    private Long bookerId;
    private BookingStatus status;
}
