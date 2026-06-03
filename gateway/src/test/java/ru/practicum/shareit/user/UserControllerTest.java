package ru.practicum.shareit.user;

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
import ru.practicum.shareit.shared.error.ErrorHandler;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
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
    UserClient userClient;

    @Test
    @DisplayName("GET /users returns users from server")
    void getUsers_returnOk() throws Exception {
        when(userClient.getUsers()).thenReturn(
                ResponseEntity.ok(List.of(new UserDto(1L, "User 1", "user1@gmail.com"))));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("user1@gmail.com"));
    }

    @Test
    @DisplayName("GET /users/{id} returns user from server")
    void getUser_returnOk() throws Exception {
        when(userClient.getUser(1L)).thenReturn(
                ResponseEntity.ok(new UserDto(1L, "User 1", "user1@gmail.com")));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("POST /users proxies valid body to server")
    void createUser_validBody_returnCreated() throws Exception {
        when(userClient.createUser(any(UserDto.class))).thenReturn(
                new ResponseEntity<>(new UserDto(1L, "New User", "new.user@gmail.com"), HttpStatus.CREATED));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UserConstantsTest.NEW_USER_DTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("new.user@gmail.com"));

        verify(userClient).createUser(any(UserDto.class));
    }

    @Test
    @DisplayName("POST /users with invalid body returns 400 without calling server")
    void createUser_invalidBody_returnBadRequest() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UserConstantsTest.INVALID_USER_DTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());

        verify(userClient, never()).createUser(any());
    }

    @Test
    @DisplayName("PATCH /users/{id} proxies update to server")
    void updateUser_returnOk() throws Exception {
        when(userClient.updateUser(eq(1L), any(UserDto.class))).thenReturn(
                ResponseEntity.ok(new UserDto(1L, "Updated Name", "new.user@gmail.com")));

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UserConstantsTest.USER_UPDATE_NAME_DTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    @DisplayName("DELETE /users/{id} proxies delete to server")
    void deleteUser_returnOk() throws Exception {
        when(userClient.deleteUser(1L)).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userClient).deleteUser(1L);
    }
}
