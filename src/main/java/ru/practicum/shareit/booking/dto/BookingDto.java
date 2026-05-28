package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.shared.dto.group.OnCreate;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
public class BookingDto {
    private Long id;

    @NotNull(groups = {OnCreate.class})
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime startRentDate;

    @NotNull(groups = {OnCreate.class})
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime endRentDate;

    @NotNull(groups = {OnCreate.class})
    private Long itemId;

    @NotNull(groups = {OnCreate.class})
    private Long bookerId;

    private BookingStatus status;
}
