package ru.practicum.shareit.item;

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
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.ClientTestSupport;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ItemClientTest {

    @Mock
    RestTemplate restTemplate;

    ItemClient itemClient;

    @BeforeEach
    void setUp() {
        itemClient = new ItemClient(ClientTestSupport.serverUrl(), ClientTestSupport.restTemplateBuilder());
        ClientTestSupport.injectRestTemplate(itemClient, restTemplate);
        ClientTestSupport.mockSuccessfulExchange(restTemplate);
    }

    @Test
    @DisplayName("ItemClient proxies item operations to server")
    void itemClient_allMethods_returnOk() {
        ItemDto itemDto = ItemConstantsTest.VALID_ITEM_DTO;
        CommentDto commentDto = ItemConstantsTest.VALID_COMMENT_DTO;

        assertEquals(HttpStatus.OK, itemClient.createItem(1L, itemDto).getStatusCode());
        assertEquals(HttpStatus.OK, itemClient.updateItem(1L, 10L, itemDto).getStatusCode());
        assertEquals(HttpStatus.OK, itemClient.createComment(1L, 10L, commentDto).getStatusCode());
        assertEquals(HttpStatus.OK, itemClient.getItem(10L).getStatusCode());
        assertEquals(HttpStatus.OK, itemClient.getOwnerItems(1L).getStatusCode());
        assertEquals(HttpStatus.OK, itemClient.searchItems(ItemConstantsTest.SEARCH_TEXT).getStatusCode());
    }

    @Test
    @DisplayName("Search with blank text returns empty list without server call")
    void searchItems_blankText_returnEmptyList() {
        ResponseEntity<Object> response = itemClient.searchItems("   ");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((List<?>) response.getBody()).isEmpty());
        verify(restTemplate, never()).exchange(
                anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class));
        verify(restTemplate, never()).exchange(
                anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class), anyMap());
    }
}
