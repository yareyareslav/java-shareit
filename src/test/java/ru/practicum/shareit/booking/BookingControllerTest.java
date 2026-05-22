package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class BookingControllerTest {

    @Autowired
    BookingController bookingController;

    @Test
    @DisplayName("Booking controller is registered in context")
    void contextLoads_controllerBeanExists() {
        assertNotNull(bookingController);
    }
}
