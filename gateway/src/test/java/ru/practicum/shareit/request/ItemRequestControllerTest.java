package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@Import(ErrorHandler.class)
class ItemRequestControllerTest {

    private static final long USER_ID = 1L;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ItemRequestClient itemRequestClient;

    @Test
    @DisplayName("GET /requests returns own requests from server")
    void getOwnRequests_returnOk() throws Exception {
        when(itemRequestClient.getOwnRequests(USER_ID)).thenReturn(
                ResponseEntity.ok(List.of(Map.of(
                        "id", ItemRequestConstantsTest.REQUEST_ID,
                        "description", ItemRequestConstantsTest.REQUEST_DESCRIPTION
                ))));

        mockMvc.perform(get("/requests").header(Headers.USER_ID, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value(ItemRequestConstantsTest.REQUEST_DESCRIPTION));
    }

    @Test
    @DisplayName("GET /requests/all returns others requests from server")
    void getOthersRequests_returnOk() throws Exception {
        when(itemRequestClient.getOthersRequests(USER_ID)).thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/requests/all").header(Headers.USER_ID, USER_ID))
                .andExpect(status().isOk());

        verify(itemRequestClient).getOthersRequests(USER_ID);
    }

    @Test
    @DisplayName("GET /requests/{id} returns request from server")
    void getRequestById_returnOk() throws Exception {
        when(itemRequestClient.getRequestById(USER_ID, ItemRequestConstantsTest.REQUEST_ID)).thenReturn(
                ResponseEntity.ok(Map.of("id", ItemRequestConstantsTest.REQUEST_ID)));

        mockMvc.perform(get("/requests/{requestId}", ItemRequestConstantsTest.REQUEST_ID)
                        .header(Headers.USER_ID, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ItemRequestConstantsTest.REQUEST_ID));
    }

    @Test
    @DisplayName("POST /requests proxies create to server")
    void addRequest_validBody_returnOk() throws Exception {
        when(itemRequestClient.addRequest(eq(USER_ID), any(ItemRequestDto.class))).thenReturn(
                new ResponseEntity<>(Map.of(
                        "id", ItemRequestConstantsTest.REQUEST_ID,
                        "description", ItemRequestConstantsTest.REQUEST_DESCRIPTION
                ), HttpStatus.OK));

        mockMvc.perform(post("/requests")
                        .header(Headers.USER_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ItemRequestConstantsTest.VALID_ITEM_REQUEST_DTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ItemRequestConstantsTest.REQUEST_ID));

        verify(itemRequestClient).addRequest(eq(USER_ID), any(ItemRequestDto.class));
    }

    @Test
    @DisplayName("POST /requests without user header returns 400")
    void addRequest_withoutHeader_returnBadRequest() throws Exception {
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ItemRequestConstantsTest.VALID_ITEM_REQUEST_DTO)))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).addRequest(anyLong(), any());
    }

    @Test
    @DisplayName("POST /requests with invalid body returns 400")
    void addRequest_invalidBody_returnBadRequest() throws Exception {
        mockMvc.perform(post("/requests")
                        .header(Headers.USER_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ItemRequestConstantsTest.INVALID_ITEM_REQUEST_DTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());

        verify(itemRequestClient, never()).addRequest(anyLong(), any());
    }
}
