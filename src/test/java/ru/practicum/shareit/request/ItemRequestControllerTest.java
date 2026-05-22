package ru.practicum.shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ItemRequestControllerTest {

    @Autowired
    ItemRequestController itemRequestController;

    @Test
    @DisplayName("Item request controller is registered in context")
    void contextLoads_controllerBeanExists() {
        assertNotNull(itemRequestController);
    }
}
