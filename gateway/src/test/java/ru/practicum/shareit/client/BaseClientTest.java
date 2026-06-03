package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BaseClientTest {

    @Mock
    RestTemplate restTemplate;

    TestClient client;

    @BeforeEach
    void setUp() {
        client = new TestClient(restTemplate);
    }

    @Test
    @DisplayName("GET without user returns successful response")
    void get_withoutUser_returnOk() {
        when(restTemplate.exchange(eq("/path"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok(Map.of("id", 1)));

        ResponseEntity<Object> response = client.getWithoutUser("/path");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("GET with user id adds header and returns response")
    void get_withUser_returnOk() {
        when(restTemplate.exchange(eq("/path"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok("ok"));

        ResponseEntity<Object> response = client.getWithUser("/path", 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("GET with uri parameters uses map overload")
    void get_withParameters_returnOk() {
        when(restTemplate.exchange(
                eq("/search?text={text}"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class),
                anyMap()
        )).thenReturn(ResponseEntity.ok("found"));

        ResponseEntity<Object> response = client.getWithParameters("/search?text={text}", Map.of("text", "дрел"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("POST PUT PATCH DELETE proxy successful responses")
    void writeMethods_success_returnOk() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok("ok"));

        assertEquals(HttpStatus.OK, client.postWithoutUser("/p", "body").getStatusCode());
        assertEquals(HttpStatus.OK, client.postWithUser("/p", 1L, "body").getStatusCode());
        assertEquals(HttpStatus.OK, client.putWithUser("/p", 1L, "body").getStatusCode());
        assertEquals(HttpStatus.OK, client.patchWithoutBody("/p", 1L).getStatusCode());
        assertEquals(HttpStatus.OK, client.patchWithBody("/p", 1L, "body").getStatusCode());
        assertEquals(HttpStatus.OK, client.deleteWithoutUser("/p").getStatusCode());
        assertEquals(HttpStatus.OK, client.deleteWithUser("/p", 1L).getStatusCode());
    }

    @Test
    @DisplayName("HttpStatusCodeException is converted to response with error body")
    void request_serverError_returnErrorResponse() {
        HttpClientErrorException exception = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND,
                "Not Found",
                null,
                "{\"error\":\"not found\"}".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenThrow(exception);

        ResponseEntity<Object> response = client.getWithoutUser("/missing");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("{\"error\":\"not found\"}", response.getBody());
    }

    @Test
    @DisplayName("Non-success response without exception is passed through")
    void request_nonSuccessStatus_returnSameStatus() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(new ResponseEntity<>("error", HttpStatus.BAD_REQUEST));

        ResponseEntity<Object> response = client.getWithoutUser("/bad");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("error", response.getBody());
    }

    @Test
    @DisplayName("Non-success response without body returns status only")
    void request_nonSuccessWithoutBody_returnStatusOnly() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());

        ResponseEntity<Object> response = client.getWithoutUser("/bad");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("POST with user and parameters uses map overload")
    void post_withUserAndParameters_returnOk() {
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Object.class),
                anyMap()
        )).thenReturn(ResponseEntity.ok("created"));

        ResponseEntity<Object> response = client.postWithUserAndParameters(
                "/items?state={state}", 1L, Map.of("state", "ALL"), "body");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(restTemplate).exchange(
                eq("/items?state={state}"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Object.class),
                anyMap()
        );
    }

    private static final class TestClient extends BaseClient {
        TestClient(RestTemplate restTemplate) {
            super(restTemplate);
        }

        ResponseEntity<Object> getWithoutUser(String path) {
            return get(path);
        }

        ResponseEntity<Object> getWithUser(String path, long userId) {
            return get(path, userId);
        }

        ResponseEntity<Object> getWithParameters(String path, Map<String, Object> parameters) {
            return get(path, null, parameters);
        }

        <T> ResponseEntity<Object> postWithoutUser(String path, T body) {
            return post(path, body);
        }

        <T> ResponseEntity<Object> postWithUser(String path, long userId, T body) {
            return post(path, userId, body);
        }

        <T> ResponseEntity<Object> postWithUserAndParameters(
                String path, long userId, Map<String, Object> parameters, T body
        ) {
            return post(path, userId, parameters, body);
        }

        <T> ResponseEntity<Object> putWithUser(String path, long userId, T body) {
            return put(path, userId, body);
        }

        ResponseEntity<Object> patchWithoutBody(String path, long userId) {
            return patch(path, userId);
        }

        <T> ResponseEntity<Object> patchWithBody(String path, long userId, T body) {
            return patch(path, userId, body);
        }

        ResponseEntity<Object> deleteWithoutUser(String path) {
            return delete(path);
        }

        ResponseEntity<Object> deleteWithUser(String path, long userId) {
            return delete(path, userId);
        }
    }
}
