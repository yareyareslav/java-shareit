package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.shared.constant.Headers;
import ru.practicum.shareit.shared.error.ErrorHandler;
import ru.practicum.shareit.shared.error.NotFoundException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@Import(ErrorHandler.class)
class ItemRequestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ItemRequestService itemRequestService;

    @Test
    @DisplayName("GET /requests returns own requests")
    void getOwnRequests_returnOk() throws Exception {
        when(itemRequestService.getOwnRequests(ItemRequestConstantsTest.REQUESTER.getId()))
                .thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/requests")
                        .header(Headers.USER_ID, ItemRequestConstantsTest.REQUESTER.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value(ItemRequestConstantsTest.REQUEST_DESCRIPTION));
    }

    @Test
    @DisplayName("GET /requests without user header returns 400")
    void getOwnRequests_withoutHeader_returnBadRequest() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /requests for missing user returns 404")
    void getOwnRequests_userNotFound_return404() throws Exception {
        when(itemRequestService.getOwnRequests(ItemRequestConstantsTest.REQUESTER.getId()))
                .thenThrow(new NotFoundException("not found"));

        mockMvc.perform(get("/requests")
                        .header(Headers.USER_ID, ItemRequestConstantsTest.REQUESTER.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not found"));
    }

    @Test
    @DisplayName("GET /requests/all returns others requests")
    void getOthersRequests_returnOk() throws Exception {
        when(itemRequestService.getOthersRequests(ItemRequestConstantsTest.REQUESTER.getId()))
                .thenReturn(List.of(otherUserResponse()));

        mockMvc.perform(get("/requests/all")
                        .header(Headers.USER_ID, ItemRequestConstantsTest.REQUESTER.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value(ItemRequestConstantsTest.OTHER_REQUEST_DESCRIPTION));

        verify(itemRequestService).getOthersRequests(ItemRequestConstantsTest.REQUESTER.getId());
    }

    @Test
    @DisplayName("GET /requests/all without user header returns 400")
    void getOthersRequests_withoutHeader_returnBadRequest() throws Exception {
        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /requests/all for missing user returns 404")
    void getOthersRequests_userNotFound_return404() throws Exception {
        when(itemRequestService.getOthersRequests(ItemRequestConstantsTest.REQUESTER.getId()))
                .thenThrow(new NotFoundException("not found"));

        mockMvc.perform(get("/requests/all")
                        .header(Headers.USER_ID, ItemRequestConstantsTest.REQUESTER.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /requests creates request")
    void addRequest_validBody_returnOk() throws Exception {
        when(itemRequestService.addRequest(
                eq(ItemRequestConstantsTest.REQUESTER.getId()), any(ItemRequestDto.class)))
                .thenReturn(sampleResponse());

        mockMvc.perform(post("/requests")
                        .header(Headers.USER_ID, ItemRequestConstantsTest.REQUESTER.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ItemRequestConstantsTest.VALID_ITEM_REQUEST_DTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(ItemRequestConstantsTest.REQUEST_DESCRIPTION));
    }

    @Test
    @DisplayName("POST /requests with invalid body returns 400")
    void addRequest_invalidBody_returnBadRequest() throws Exception {
        ItemRequestDto invalidDto = new ItemRequestDto(null, "   ");

        mockMvc.perform(post("/requests")
                        .header(Headers.USER_ID, ItemRequestConstantsTest.REQUESTER.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("POST /requests without user header returns 400")
    void addRequest_withoutHeader_returnBadRequest() throws Exception {
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ItemRequestConstantsTest.VALID_ITEM_REQUEST_DTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /requests for missing user returns 404")
    void addRequest_userNotFound_return404() throws Exception {
        when(itemRequestService.addRequest(
                eq(ItemRequestConstantsTest.REQUESTER.getId()), any(ItemRequestDto.class)))
                .thenThrow(new NotFoundException("not found"));

        mockMvc.perform(post("/requests")
                        .header(Headers.USER_ID, ItemRequestConstantsTest.REQUESTER.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ItemRequestConstantsTest.VALID_ITEM_REQUEST_DTO)))
                .andExpect(status().isNotFound());
    }

    private ResponseItemRequestDto sampleResponse() {
        return new ResponseItemRequestDto(
                ItemRequestConstantsTest.REQUEST_DESCRIPTION,
                ItemRequestConstantsTest.REQUEST_CREATED,
                List.of()
        );
    }

    private ResponseItemRequestDto otherUserResponse() {
        return new ResponseItemRequestDto(
                ItemRequestConstantsTest.OTHER_REQUEST_DESCRIPTION,
                ItemRequestConstantsTest.REQUEST_CREATED.plusHours(1),
                List.of()
        );
    }
}
