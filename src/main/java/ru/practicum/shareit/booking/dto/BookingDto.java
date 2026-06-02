package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.shared.dto.group.OnCreate;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
public class BookingDto {
    private Long id;

    @NotNull(groups = {OnCreate.class})
    @FutureOrPresent(groups = {OnCreate.class})
    private LocalDateTime start;

    @NotNull(groups = {OnCreate.class})
    @FutureOrPresent(groups = {OnCreate.class})
    private LocalDateTime end;

    @NotNull(groups = {OnCreate.class})
    private Long itemId;

    private Long bookerId;

    private BookingStatus status;
}
