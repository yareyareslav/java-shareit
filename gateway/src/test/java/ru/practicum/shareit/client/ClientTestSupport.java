package ru.practicum.shareit.client;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;

public final class ClientTestSupport {
    private static final String SERVER_URL = "http://localhost:9090";

    private ClientTestSupport() {
    }

    public static void mockSuccessfulExchange(RestTemplate restTemplate) {
        lenient().when(restTemplate.exchange(
                anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok("ok"));
        lenient().when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(Object.class),
                anyMap()
        )).thenReturn(ResponseEntity.ok("ok"));
    }

    public static void injectRestTemplate(BaseClient client, RestTemplate restTemplate) {
        ReflectionTestUtils.setField(client, "rest", restTemplate);
    }

    public static RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder();
    }

    public static String serverUrl() {
        return SERVER_URL;
    }
}
