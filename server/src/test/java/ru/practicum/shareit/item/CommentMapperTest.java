package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingConstantsTest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CommentMapperTest {

    @Test
    @DisplayName("Map comment dto to comment entity")
    void toComment_mapDto_returnComment() {
        Item item = ItemConstantsTest.createItem(1L, "Дрель", "Описание", true, ItemConstantsTest.OWNER);
        CommentDto dto = new CommentDto(null, null, "Текст");

        Comment comment = CommentMapper.toComment(dto, BookingConstantsTest.BOOKER, item);

        assertEquals("Текст", comment.getText());
        assertEquals(BookingConstantsTest.BOOKER, comment.getAuthor());
        assertEquals(item, comment.getItem());
    }

    @Test
    @DisplayName("Map comment to comment dto")
    void toCommentDto_mapComment_returnDto() {
        Item item = ItemConstantsTest.createItem(1L, "Дрель", "Описание", true, ItemConstantsTest.OWNER);
        Comment comment = new Comment(1L, BookingConstantsTest.BOOKER, item, "Текст", Instant.now());

        CommentDto dto = CommentMapper.toCommentDto(comment);

        assertEquals(1L, dto.getId());
        assertEquals(BookingConstantsTest.BOOKER.getName(), dto.getAuthorName());
        assertEquals("Текст", dto.getText());
    }

    @Test
    @DisplayName("Map comment without author to comment dto")
    void toCommentDto_commentWithoutAuthor_returnDtoWithNullAuthor() {
        Item item = ItemConstantsTest.createItem(1L, "Дрель", "Описание", true, ItemConstantsTest.OWNER);
        Comment comment = new Comment(1L, null, item, "Текст", Instant.now());

        CommentDto dto = CommentMapper.toCommentDto(comment);

        assertNull(dto.getAuthorName());
    }

    @Test
    @DisplayName("Map comment to response comment dto")
    void toResponseCommentDto_mapComment_returnResponseDto() {
        Item item = ItemConstantsTest.createItem(1L, "Дрель", "Описание", true, ItemConstantsTest.OWNER);
        Instant created = Instant.now();
        Comment comment = new Comment(1L, BookingConstantsTest.BOOKER, item, "Текст", created);

        ResponseCommentDto dto = CommentMapper.toResponseCommentDto(comment);

        assertEquals(1L, dto.getId());
        assertEquals(BookingConstantsTest.BOOKER.getName(), dto.getAuthorName());
        assertEquals("Текст", dto.getText());
        assertEquals(created, dto.getCreated());
    }
}
