package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.shared.constant.Headers;
import ru.practicum.shareit.shared.error.ErrorHandler;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@Import(ErrorHandler.class)
class ItemControllerTest {

    private static final long USER_ID = 1L;
    private static final long ITEM_ID = 10L;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ItemClient itemClient;

    @Test
    @DisplayName("POST /items proxies create to server")
    void createItem_validBody_returnCreated() throws Exception {
        when(itemClient.createItem(eq(USER_ID), any(ItemDto.class))).thenReturn(
                new ResponseEntity<>(new ItemDto(ITEM_ID, "Дрель", "Описание", true, USER_ID, null),
                        HttpStatus.CREATED));

        mockMvc.perform(post("/items")
                        .header(Headers.USER_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ItemConstantsTest.VALID_ITEM_DTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(ITEM_ID));

        verify(itemClient).createItem(eq(USER_ID), any(ItemDto.class));
    }

    @Test
    @DisplayName("POST /items without user header returns 400")
    void createItem_withoutHeader_returnBadRequest() throws Exception {
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ItemConstantsTest.VALID_ITEM_DTO)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createItem(anyLong(), any());
    }

    @Test
    @DisplayName("POST /items with invalid body returns 400")
    void createItem_invalidBody_returnBadRequest() throws Exception {
        mockMvc.perform(post("/items")
                        .header(Headers.USER_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ItemConstantsTest.INVALID_ITEM_DTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());

        verify(itemClient, never()).createItem(anyLong(), any());
    }

    @Test
    @DisplayName("PATCH /items/{id} proxies update to server")
    void updateItem_returnOk() throws Exception {
        when(itemClient.updateItem(eq(USER_ID), eq(ITEM_ID), any(ItemDto.class))).thenReturn(
                ResponseEntity.ok(new ItemDto(ITEM_ID, "Отвёртка", "Новое описание", false, USER_ID, null)));

        mockMvc.perform(patch("/items/{itemId}", ITEM_ID)
                        .header(Headers.USER_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ItemConstantsTest.ITEM_UPDATE_DTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Отвёртка"));
    }

    @Test
    @DisplayName("GET /items/{id} returns item from server")
    void getItem_returnOk() throws Exception {
        when(itemClient.getItem(ITEM_ID)).thenReturn(
                ResponseEntity.ok(Map.of("id", ITEM_ID, "name", "Дрель")));

        mockMvc.perform(get("/items/{itemId}", ITEM_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ITEM_ID));
    }

    @Test
    @DisplayName("GET /items returns owner items from server")
    void getOwnerItems_returnOk() throws Exception {
        when(itemClient.getOwnerItems(USER_ID)).thenReturn(
                ResponseEntity.ok(List.of(Map.of("id", ITEM_ID, "name", "Дрель"))));

        mockMvc.perform(get("/items").header(Headers.USER_ID, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(ITEM_ID));
    }

    @Test
    @DisplayName("GET /items/search proxies search to server")
    void searchItems_returnOk() throws Exception {
        when(itemClient.searchItems(ItemConstantsTest.SEARCH_TEXT)).thenReturn(
                ResponseEntity.ok(List.of(new ItemDto(ITEM_ID, "Дрель", "Описание", true, null, null))));

        mockMvc.perform(get("/items/search").param("text", ItemConstantsTest.SEARCH_TEXT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Дрель"));
    }

    @Test
    @DisplayName("POST /items/{id}/comment proxies comment to server")
    void createComment_returnCreated() throws Exception {
        when(itemClient.createComment(eq(USER_ID), eq(ITEM_ID), any())).thenReturn(
                new ResponseEntity<>(Map.of("id", 1, "text", "Отличная вещь"), HttpStatus.CREATED));

        mockMvc.perform(post("/items/{itemId}/comment", ITEM_ID)
                        .header(Headers.USER_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ItemConstantsTest.VALID_COMMENT_DTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.text").value("Отличная вещь"));
    }
}
