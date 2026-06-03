package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.shared.dto.group.OnCreate;
import ru.practicum.shareit.shared.dto.group.OnUpdate;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@AllArgsConstructor
public class ItemRequestDto {
    @NotNull(groups = {OnUpdate.class})
    private Long id;
    @NotBlank(groups = {OnCreate.class})
    private String description;
}