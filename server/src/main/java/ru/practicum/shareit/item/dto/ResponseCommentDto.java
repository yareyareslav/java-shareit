package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class ResponseCommentDto {
    private Long id;

    private String authorName;

    private String text;

    private Instant created;
}
