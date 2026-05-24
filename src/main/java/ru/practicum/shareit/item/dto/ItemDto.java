package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.shared.dto.group.OnCreate;
import ru.practicum.shareit.shared.dto.group.OnUpdate;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    @NotNull(groups = {OnUpdate.class})
    private Long id;
    @NotBlank(groups = {OnCreate.class})
    private String name;
    @NotBlank(groups = {OnCreate.class})
    private String description;
    @NotNull(groups = {OnCreate.class})
    private Boolean available;
    private Long owner;
    private Long request;
}