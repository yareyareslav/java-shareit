package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("Find existing user by email")
    void getByEmail_getExistingUserByEmail_returnExistingUserWithProvidedEmail() {
        userRepository.save(UserConstantsTest.VALID_USER_1);

        Optional<User> user = userRepository.findByEmail(UserConstantsTest.VALID_USER_1.getEmail());
        assertTrue(user.isPresent());
        assertEquals(UserConstantsTest.VALID_USER_1.getId(), user.get().getId());
        assertEquals(UserConstantsTest.VALID_USER_1.getName(), user.get().getName());
        assertEquals(UserConstantsTest.VALID_USER_1.getEmail(), user.get().getEmail());
    }
}
