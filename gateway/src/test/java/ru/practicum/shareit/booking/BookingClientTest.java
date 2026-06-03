package ru.practicum.shareit.booking;

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
class BookingClientTest {

    @Mock
    RestTemplate restTemplate;

    BookingClient bookingClient;

    @BeforeEach
    void setUp() {
        bookingClient = new BookingClient(ClientTestSupport.serverUrl(), ClientTestSupport.restTemplateBuilder());
        ClientTestSupport.injectRestTemplate(bookingClient, restTemplate);
        ClientTestSupport.mockSuccessfulExchange(restTemplate);
    }

    @Test
    @DisplayName("BookingClient proxies booking operations to server")
    void bookingClient_allMethods_returnOk() {
        assertEquals(HttpStatus.OK, bookingClient.getBookings(1L, BookingState.ALL).getStatusCode());
        assertEquals(HttpStatus.OK, bookingClient.getOwnerBookings(1L, BookingState.PAST).getStatusCode());
        assertEquals(HttpStatus.OK, bookingClient.bookItem(1L, BookingConstantsTest.VALID_BOOKING_DTO).getStatusCode());
        assertEquals(HttpStatus.OK, bookingClient.getBooking(1L, BookingConstantsTest.BOOKING_ID).getStatusCode());
        assertEquals(HttpStatus.OK,
                bookingClient.approveBooking(1L, BookingConstantsTest.BOOKING_ID, true).getStatusCode());
    }
}
