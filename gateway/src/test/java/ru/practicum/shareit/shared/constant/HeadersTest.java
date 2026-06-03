package ru.practicum.shareit.shared.constant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HeadersTest {

    @Test
    @DisplayName("User id header constant")
    void userIdHeader_hasExpectedValue() {
        assertEquals("X-Sharer-User-Id", Headers.USER_ID);
    }
}
