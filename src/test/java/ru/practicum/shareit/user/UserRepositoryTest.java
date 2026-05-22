package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("Find all users")
    void findAll_getAllUsers_returnTwoUsers() {
        userRepository.save(UserConstantsTest.VALID_USER_1);
        userRepository.save(UserConstantsTest.VALID_USER_2);

        List<User> users = userRepository.findAll();
        assertEquals(2, users.size());
    }

    @Test
    @DisplayName("Find user by id")
    void getById_getExistingUserById_returnExistingUserWithProvidedId() {
        userRepository.save(UserConstantsTest.VALID_USER_1);

        Optional<User> user = userRepository.findById(UserConstantsTest.VALID_USER_1.getId());
        assertTrue(user.isPresent());
        assertEquals(UserConstantsTest.VALID_USER_1.getId(), user.get().getId());
        assertEquals(UserConstantsTest.VALID_USER_1.getName(), user.get().getName());
        assertEquals(UserConstantsTest.VALID_USER_1.getEmail(), user.get().getEmail());
    }

    @Test
    @DisplayName("Find non-existing user by id")
    void getById_getNonExistingUserById_returnOptionalEmpty() {
        Optional<User> user = userRepository.findById(999L);

        assertTrue(user.isEmpty());
    }

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

    @Test
    @DisplayName("Find non-existing user by email")
    void getByEmail_getNonExistingUserByEmail_returnOptionalEmpty() {
        Optional<User> user = userRepository.findByEmail(UserConstantsTest.NON_EXISTING_EMAIL);

        assertTrue(user.isEmpty());
    }

    @Test
    @DisplayName("Save user with id")
    void save_saveUserWithId_returnUser() {
        User user = userRepository.save(UserConstantsTest.VALID_USER_1);

        assertEquals(UserConstantsTest.VALID_USER_1.getId(), user.getId());
        assertEquals(UserConstantsTest.VALID_USER_1.getName(), user.getName());
        assertEquals(UserConstantsTest.VALID_USER_1.getEmail(), user.getEmail());
    }

    @Test
    @DisplayName("Save user without id assigns generated id")
    void save_saveUserWithoutId_assignsId() {
        User user = userRepository.save(UserConstantsTest.NEW_USER);

        assertNotNull(user.getId());
        assertEquals(UserConstantsTest.NEW_USER.getName(), user.getName());
        assertEquals(UserConstantsTest.NEW_USER.getEmail(), user.getEmail());
    }

    @Test
    @DisplayName("Update user")
    void update_updateExistingUser_returnUpdatedUser() {
        userRepository.save(UserConstantsTest.VALID_USER_1);
        User user = userRepository.findById(1L).orElseThrow();
        user.setName("Updated");

        User updated = userRepository.update(user);

        assertEquals("Updated", updated.getName());
        assertEquals(UserConstantsTest.VALID_USER_1.getEmail(), updated.getEmail());
    }

    @Test
    @DisplayName("Delete user by id")
    void deleteById_deleteExistingUser_userNotFoundAfterDelete() {
        userRepository.save(UserConstantsTest.VALID_USER_1);

        userRepository.deleteById(UserConstantsTest.VALID_USER_1.getId());

        assertTrue(userRepository.findById(UserConstantsTest.VALID_USER_1.getId()).isEmpty());
    }
}
