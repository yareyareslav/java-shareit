package ru.practicum.shareit.shared.error;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.shared.constant.Headers;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.UserController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {UserController.class, BookingController.class})
@Import(ErrorHandler.class)
class ErrorHandlerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserClient userClient;

    @MockBean
    BookingClient bookingClient;

    @Test
    @DisplayName("Validation error on POST /users returns 400 with error message")
    void handleValidationOnCreateUser_return400() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"email\":\"bad\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("Missing header on GET /bookings returns 400")
    void handleMissingHeader_return400() throws Exception {
        mockMvc.perform(get("/bookings"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("IllegalArgumentException on GET /bookings returns 400")
    void handleIllegalArgument_return400() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header(Headers.USER_ID, 1L)
                        .param("state", "INVALID"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Unknown state: INVALID"));
    }
}
