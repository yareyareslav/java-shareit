package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.shared.error.ErrorHandler;
import ru.practicum.shareit.shared.error.ForbiddenException;
import ru.practicum.shareit.shared.error.NotFoundException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@Import(ErrorHandler.class)
class ItemControllerTest {

    private static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ItemService itemService;

    @Test
    @DisplayName("POST /items creates item")
    void createItem_validRequest_returnCreated() throws Exception {
        when(itemService.createItem(eq(1L), any(ItemDto.class)))
                .thenReturn(new ItemDto(1L, "Дрель", "Описание", true, null, null));

        mockMvc.perform(post("/items")
                        .header(SHARER_USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ItemConstantsTest.VALID_ITEM_DTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Дрель"));
    }

    @Test
    @DisplayName("POST /items without required fields returns 400")
    void createItem_invalidBody_returnBadRequest() throws Exception {
        ItemDto invalidDto = new ItemDto(null, "", "", null, null, null);

        mockMvc.perform(post("/items")
                        .header(SHARER_USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("PATCH /items/{id} updates item")
    void updateItem_returnOk() throws Exception {
        when(itemService.updateItem(eq(1L), eq(1L), any(ItemDto.class)))
                .thenReturn(new ItemDto(1L, "Отвёртка", "Описание", false, null, null));

        mockMvc.perform(patch("/items/1")
                        .header(SHARER_USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ItemConstantsTest.ITEM_UPDATE_DTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Отвёртка"));
    }

    @Test
    @DisplayName("PATCH /items/{id} by non-owner returns 403")
    void updateItem_forbidden_return403() throws Exception {
        when(itemService.updateItem(eq(1L), eq(1L), any(ItemDto.class)))
                .thenThrow(new ForbiddenException("forbidden"));

        mockMvc.perform(patch("/items/1")
                        .header(SHARER_USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ItemConstantsTest.ITEM_UPDATE_DTO)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("forbidden"));
    }

    @Test
    @DisplayName("GET /items/{id} returns item")
    void getItem_returnOk() throws Exception {
        when(itemService.getItemById(1L)).thenReturn(new ItemDto(1L, "Дрель", "Описание", true, null, null));

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /items returns owner items")
    void getOwnerItems_returnOk() throws Exception {
        when(itemService.getItemsByOwner(1L))
                .thenReturn(List.of(new ItemDto(1L, "Дрель", "Описание", true, null, null)));

        mockMvc.perform(get("/items").header(SHARER_USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Дрель"));
    }

    @Test
    @DisplayName("GET /items/search returns found items")
    void searchItems_returnOk() throws Exception {
        when(itemService.searchItems("дрел"))
                .thenReturn(List.of(new ItemDto(1L, "Дрель", "Описание", true, null, null)));

        mockMvc.perform(get("/items/search").param("text", "дрел"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Дрель"));
    }

    @Test
    @DisplayName("GET /items/{id} for missing item returns 404")
    void getItem_notFound_return404() throws Exception {
        when(itemService.getItemById(99L)).thenThrow(new NotFoundException("not found"));

        mockMvc.perform(get("/items/99"))
                .andExpect(status().isNotFound());
    }
}
