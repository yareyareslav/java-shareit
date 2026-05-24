package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.shared.error.ErrorHandler;
import ru.practicum.shareit.shared.error.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(ErrorHandler.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    @Test
    @DisplayName("GET /users returns all users")
    void getAllUsers_returnOkAndList() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(
                new UserDto(1L, "User 1", "user1@gmail.com")
        ));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("user1@gmail.com"));
    }

    @Test
    @DisplayName("GET /users/{id} returns user")
    void getUser_returnOk() throws Exception {
        when(userService.getUserById(1L)).thenReturn(new UserDto(1L, "User 1", "user1@gmail.com"));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /users/{id} for missing user returns 404")
    void getUser_notFound_return404() throws Exception {
        when(userService.getUserById(99L)).thenThrow(new NotFoundException("not found"));

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not found"));
    }

    @Test
    @DisplayName("POST /users creates user")
    void createUser_validBody_returnCreated() throws Exception {
        when(userService.createUser(any(UserDto.class)))
                .thenReturn(new UserDto(1L, "User 1", "user1@gmail.com"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UserConstantsTest.NEW_USER_DTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("user1@gmail.com"));
    }

    @Test
    @DisplayName("POST /users with invalid email returns 400")
    void createUser_invalidEmail_returnBadRequest() throws Exception {
        UserDto invalidUser = new UserDto(null, "User", "not-an-email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("PATCH /users/{id} updates user")
    void updateUser_returnOk() throws Exception {
        when(userService.updateUser(eq(1L), any(UserDto.class)))
                .thenReturn(new UserDto(1L, "Updated", "user1@gmail.com"));

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UserConstantsTest.USER_UPDATE_NAME_DTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    @DisplayName("DELETE /users/{id} deletes user")
    void deleteUser_returnOk() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService).deleteUser(1L);
    }

    @Test
    @DisplayName("DELETE /users/{id} for missing user returns 404")
    void deleteUser_notFound_return404() throws Exception {
        doThrow(new NotFoundException("not found")).when(userService).deleteUser(99L);

        mockMvc.perform(delete("/users/99"))
                .andExpect(status().isNotFound());
    }
}
