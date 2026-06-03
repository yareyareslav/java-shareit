package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemConstantsTest;
import ru.practicum.shareit.shared.constant.Headers;
import ru.practicum.shareit.shared.error.ErrorHandler;
import ru.practicum.shareit.shared.error.ForbiddenException;
import ru.practicum.shareit.shared.error.NotFoundException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BookingService bookingService;

    @Test
    @DisplayName("GET /bookings returns booker bookings")
    void getBookings_returnOk() throws Exception {
        when(bookingService.getBookings(eq(2L), eq(BookingState.ALL)))
                .thenReturn(List.of(sampleResponseBooking()));

        mockMvc.perform(get("/bookings")
                        .header(Headers.USER_ID, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(BookingConstantsTest.LAST_BOOKING_ID))
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }

    @Test
    @DisplayName("GET /bookings with state filter delegates state to service")
    void getBookings_withState_passesStateToService() throws Exception {
        when(bookingService.getBookings(2L, BookingState.PAST)).thenReturn(List.of());

        mockMvc.perform(get("/bookings")
                        .header(Headers.USER_ID, 2L)
                        .param("state", "PAST"))
                .andExpect(status().isOk());

        verify(bookingService).getBookings(2L, BookingState.PAST);
    }

    @Test
    @DisplayName("GET /bookings without user header returns 400")
    void getBookings_withoutHeader_returnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /bookings/owner returns owner item bookings")
    void getBookingsOfOwner_returnOk() throws Exception {
        when(bookingService.getBookingsOfItemsOwnedByUser(eq(1L), eq(BookingState.ALL)))
                .thenReturn(List.of(sampleResponseBooking()));

        mockMvc.perform(get("/bookings/owner")
                        .header(Headers.USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(BookingConstantsTest.LAST_BOOKING_ID));
    }

    @Test
    @DisplayName("GET /bookings/{id} returns booking")
    void getBookingById_returnOk() throws Exception {
        when(bookingService.getBookingById(BookingConstantsTest.LAST_BOOKING_ID, 2L))
                .thenReturn(sampleResponseBooking());

        mockMvc.perform(get("/bookings/" + BookingConstantsTest.LAST_BOOKING_ID)
                        .header(Headers.USER_ID, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(BookingConstantsTest.LAST_BOOKING_ID))
                .andExpect(jsonPath("$.item.id").value(BookingConstantsTest.DEFAULT_ITEM_ID));
    }

    @Test
    @DisplayName("GET /bookings/{id} for missing booking returns 404")
    void getBookingById_notFound_return404() throws Exception {
        when(bookingService.getBookingById(BookingConstantsTest.NON_EXISTING_BOOKING_ID, 2L))
                .thenThrow(new NotFoundException("not found"));

        mockMvc.perform(get("/bookings/" + BookingConstantsTest.NON_EXISTING_BOOKING_ID)
                        .header(Headers.USER_ID, 2L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not found"));
    }

    @Test
    @DisplayName("GET /bookings/{id} for unrelated user returns 403")
    void getBookingById_forbidden_return403() throws Exception {
        when(bookingService.getBookingById(BookingConstantsTest.LAST_BOOKING_ID, 3L))
                .thenThrow(new ForbiddenException("forbidden"));

        mockMvc.perform(get("/bookings/" + BookingConstantsTest.LAST_BOOKING_ID)
                        .header(Headers.USER_ID, 3L))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("forbidden"));
    }

    @Test
    @DisplayName("POST /bookings creates booking")
    void createBooking_validBody_returnOk() throws Exception {
        when(bookingService.create(eq(2L), any(BookingDto.class)))
                .thenReturn(sampleResponseBooking());

        mockMvc.perform(post("/bookings")
                        .header(Headers.USER_ID, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BookingConstantsTest.VALID_BOOKING_DTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(BookingConstantsTest.LAST_BOOKING_ID))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    @DisplayName("POST /bookings with invalid body returns 400")
    void createBooking_invalidBody_returnBadRequest() throws Exception {
        BookingDto invalidDto = new BookingDto(null, null, null, null, null, null);

        mockMvc.perform(post("/bookings")
                        .header(Headers.USER_ID, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("POST /bookings without user header returns 400")
    void createBooking_withoutHeader_returnBadRequest() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BookingConstantsTest.VALID_BOOKING_DTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /bookings/{id} approves booking")
    void updateStatus_approved_returnOk() throws Exception {
        ResponseBookingDto approved = sampleResponseBooking();
        approved.setStatus(BookingStatus.APPROVED);
        when(bookingService.updateStatus(1L, BookingConstantsTest.LAST_BOOKING_ID, true))
                .thenReturn(approved);

        mockMvc.perform(patch("/bookings/" + BookingConstantsTest.LAST_BOOKING_ID)
                        .header(Headers.USER_ID, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @DisplayName("PATCH /bookings/{id} rejects booking")
    void updateStatus_rejected_returnOk() throws Exception {
        ResponseBookingDto rejected = sampleResponseBooking();
        rejected.setStatus(BookingStatus.REJECTED);
        when(bookingService.updateStatus(1L, BookingConstantsTest.LAST_BOOKING_ID, false))
                .thenReturn(rejected);

        mockMvc.perform(patch("/bookings/" + BookingConstantsTest.LAST_BOOKING_ID)
                        .header(Headers.USER_ID, 1L)
                        .param("approved", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    @DisplayName("PATCH /bookings/{id} for missing booking returns 404")
    void updateStatus_notFound_return404() throws Exception {
        when(bookingService.updateStatus(1L, BookingConstantsTest.NON_EXISTING_BOOKING_ID, true))
                .thenThrow(new NotFoundException("not found"));

        mockMvc.perform(patch("/bookings/" + BookingConstantsTest.NON_EXISTING_BOOKING_ID)
                        .header(Headers.USER_ID, 1L)
                        .param("approved", "true"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /bookings/{id} for unknown user returns 403")
    void updateStatus_forbidden_return403() throws Exception {
        when(bookingService.updateStatus(99L, BookingConstantsTest.LAST_BOOKING_ID, true))
                .thenThrow(new ForbiddenException("forbidden"));

        mockMvc.perform(patch("/bookings/" + BookingConstantsTest.LAST_BOOKING_ID)
                        .header(Headers.USER_ID, 99L)
                        .param("approved", "true"))
                .andExpect(status().isForbidden());
    }

    private ResponseBookingDto sampleResponseBooking() {
        Item item = ItemConstantsTest.createItem(
                BookingConstantsTest.DEFAULT_ITEM_ID,
                "Дрель",
                "Описание",
                true,
                ItemConstantsTest.OWNER
        );
        return new ResponseBookingDto(
                BookingConstantsTest.LAST_BOOKING_ID,
                BookingConstantsTest.FUTURE_START,
                BookingConstantsTest.FUTURE_END,
                item,
                BookingConstantsTest.BOOKER,
                BookingStatus.WAITING
        );
    }
}
