package ru.practicum.shareit.shared.error;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@Import(ErrorHandler.class)
class ErrorHandlerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Test
    @DisplayName("Handle NotFoundException")
    void handleNotFoundException_return404() throws Exception {
        when(userService.getUserById(1L)).thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Пользователь не найден"));
    }

    @Test
    @DisplayName("Handle ConflictException")
    void handleConflictException_return409() throws Exception {
        when(userService.createUser(any(UserDto.class)))
                .thenThrow(new ConflictException("Email уже занят"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"User\",\"email\":\"user@gmail.com\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Email уже занят"));
    }

    @Test
    @DisplayName("Handle BadRequestException")
    void handleBadRequestException_return400() throws Exception {
        when(userService.updateUser(any()))
                .thenThrow(new BadRequestException("Некорректный запрос"));

        mockMvc.perform(patch("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("must not be null"));
    }
}
