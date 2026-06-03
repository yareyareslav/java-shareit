package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.ClientTestSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ItemRequestClientTest {

    @Mock
    RestTemplate restTemplate;

    ItemRequestClient itemRequestClient;

    @BeforeEach
    void setUp() {
        itemRequestClient = new ItemRequestClient(
                ClientTestSupport.serverUrl(), ClientTestSupport.restTemplateBuilder());
        ClientTestSupport.injectRestTemplate(itemRequestClient, restTemplate);
        ClientTestSupport.mockSuccessfulExchange(restTemplate);
    }

    @Test
    @DisplayName("ItemRequestClient proxies request operations to server")
    void itemRequestClient_allMethods_returnOk() {
        assertEquals(HttpStatus.OK, itemRequestClient.getOwnRequests(1L).getStatusCode());
        assertEquals(HttpStatus.OK, itemRequestClient.getOthersRequests(1L).getStatusCode());
        assertEquals(HttpStatus.OK, itemRequestClient.getRequestById(1L, 10L).getStatusCode());
        assertEquals(HttpStatus.OK,
                itemRequestClient.addRequest(1L, ItemRequestConstantsTest.VALID_ITEM_REQUEST_DTO).getStatusCode());
    }
}
