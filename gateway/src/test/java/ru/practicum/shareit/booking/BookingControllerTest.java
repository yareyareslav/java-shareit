package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.shared.constant.Headers;
import ru.practicum.shareit.shared.error.ErrorHandler;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@Import(ErrorHandler.class)
class BookingControllerTest {

    private static final long USER_ID = 2L;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BookingClient bookingClient;

    @Test
    @DisplayName("GET /bookings returns bookings from server")
    void getBookings_returnOk() throws Exception {
        when(bookingClient.getBookings(USER_ID, BookingState.ALL)).thenReturn(
                ResponseEntity.ok(List.of(Map.of("id", BookingConstantsTest.BOOKING_ID, "status", "WAITING"))));

        mockMvc.perform(get("/bookings").header(Headers.USER_ID, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(BookingConstantsTest.BOOKING_ID));

        verify(bookingClient).getBookings(USER_ID, BookingState.ALL);
    }

    @Test
    @DisplayName("GET /bookings with state passes state to client")
    void getBookings_withState_passesStateToClient() throws Exception {
        when(bookingClient.getBookings(USER_ID, BookingState.PAST)).thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/bookings")
                        .header(Headers.USER_ID, USER_ID)
                        .param("state", "PAST"))
                .andExpect(status().isOk());

        verify(bookingClient).getBookings(USER_ID, BookingState.PAST);
    }

    @Test
    @DisplayName("GET /bookings without user header returns 400")
    void getBookings_withoutHeader_returnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings"))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getBookings(any(Long.class), any());
    }

    @Test
    @DisplayName("GET /bookings with unknown state returns 400")
    void getBookings_unknownState_returnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header(Headers.USER_ID, USER_ID)
                        .param("state", "UNKNOWN"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Unknown state: UNKNOWN"));

        verify(bookingClient, never()).getBookings(any(Long.class), any());
    }

    @Test
    @DisplayName("GET /bookings/owner returns owner bookings from server")
    void getOwnerBookings_returnOk() throws Exception {
        when(bookingClient.getOwnerBookings(USER_ID, BookingState.ALL)).thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/bookings/owner").header(Headers.USER_ID, USER_ID))
                .andExpect(status().isOk());

        verify(bookingClient).getOwnerBookings(USER_ID, BookingState.ALL);
    }

    @Test
    @DisplayName("POST /bookings proxies booking to server")
    void bookItem_validBody_returnCreated() throws Exception {
        when(bookingClient.bookItem(eq(USER_ID), any(BookItemRequestDto.class))).thenReturn(
                new ResponseEntity<>(Map.of("id", BookingConstantsTest.BOOKING_ID), HttpStatus.CREATED));

        mockMvc.perform(post("/bookings")
                        .header(Headers.USER_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BookingConstantsTest.VALID_BOOKING_DTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(BookingConstantsTest.BOOKING_ID));
    }

    @Test
    @DisplayName("POST /bookings with invalid period returns 400")
    void bookItem_invalidPeriod_returnBadRequest() throws Exception {
        mockMvc.perform(post("/bookings")
                        .header(Headers.USER_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BookingConstantsTest.invalidEndBeforeStart())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());

        verify(bookingClient, never()).bookItem(any(Long.class), any());
    }

    @Test
    @DisplayName("GET /bookings/{id} returns booking from server")
    void getBooking_returnOk() throws Exception {
        when(bookingClient.getBooking(USER_ID, BookingConstantsTest.BOOKING_ID)).thenReturn(
                ResponseEntity.ok(Map.of("id", BookingConstantsTest.BOOKING_ID)));

        mockMvc.perform(get("/bookings/{bookingId}", BookingConstantsTest.BOOKING_ID)
                        .header(Headers.USER_ID, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(BookingConstantsTest.BOOKING_ID));
    }

    @Test
    @DisplayName("PATCH /bookings/{id} proxies approve to server")
    void approveBooking_returnOk() throws Exception {
        when(bookingClient.approveBooking(USER_ID, BookingConstantsTest.BOOKING_ID, true)).thenReturn(
                ResponseEntity.ok(Map.of("id", BookingConstantsTest.BOOKING_ID, "status", "APPROVED")));

        mockMvc.perform(patch("/bookings/{bookingId}", BookingConstantsTest.BOOKING_ID)
                        .header(Headers.USER_ID, USER_ID)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }
}
