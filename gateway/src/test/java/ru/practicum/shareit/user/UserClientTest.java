package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.ClientTestSupport;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserClientTest {

    @Mock
    RestTemplate restTemplate;

    UserClient userClient;

    @BeforeEach
    void setUp() {
        userClient = new UserClient(ClientTestSupport.serverUrl(), ClientTestSupport.restTemplateBuilder());
        ClientTestSupport.injectRestTemplate(userClient, restTemplate);
        ClientTestSupport.mockSuccessfulExchange(restTemplate);
    }

    @Test
    @DisplayName("UserClient proxies user operations to server")
    void userClient_allMethods_returnOk() {
        assertEquals(HttpStatus.OK, userClient.getUsers().getStatusCode());
        assertEquals(HttpStatus.OK, userClient.getUser(1L).getStatusCode());
        assertEquals(HttpStatus.OK, userClient.createUser(new UserDto(null, "User", "user@gmail.com")).getStatusCode());
        assertEquals(HttpStatus.OK, userClient.updateUser(1L, new UserDto(null, "New", null)).getStatusCode());
        assertEquals(HttpStatus.OK, userClient.deleteUser(1L).getStatusCode());
    }
}
