package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.user.User;

import java.time.Instant;

public class CommentMapper {
    public static Comment toComment(CommentDto dto, User author, Item item) {
        return new Comment(
                dto.getId(),
                author,
                item,
                dto.getText(),
                Instant.now()
        );
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getAuthor().getName(),
                comment.getText()
        );
    }

    public static ResponseCommentDto toResponseCommentDto(Comment comment) {
        return new ResponseCommentDto(
                comment.getId(),
                comment.getAuthor().getName(),
                comment.getText(),
                comment.getCreated()
        );
    }
}
