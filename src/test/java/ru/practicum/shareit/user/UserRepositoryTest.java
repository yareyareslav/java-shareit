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
    void getAll_getAllUsers_returnTwoUsers() {
        userRepository.create(UserConstantsTest.VALID_USER_1);
        userRepository.create(UserConstantsTest.VALID_USER_2);

        List<User> users = userRepository.getAll();
        assertEquals(2, users.size());
    }

    @Test
    @DisplayName("Find user by id")
    void getById_getExistingUserById_returnExistingUserWithProvidedId() {
        userRepository.create(UserConstantsTest.VALID_USER_1);

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
        userRepository.create(UserConstantsTest.VALID_USER_1);

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
    void save_createUserWithId_returnUser() {
        User user = userRepository.create(UserConstantsTest.VALID_USER_1);

        assertEquals(UserConstantsTest.VALID_USER_1.getId(), user.getId());
        assertEquals(UserConstantsTest.VALID_USER_1.getName(), user.getName());
        assertEquals(UserConstantsTest.VALID_USER_1.getEmail(), user.getEmail());
    }

    @Test
    @DisplayName("Save user without id assigns generated id")
    void save_createUserWithoutId_assignsId() {
        User user = userRepository.create(UserConstantsTest.NEW_USER);

        assertNotNull(user.getId());
        assertEquals(UserConstantsTest.NEW_USER.getName(), user.getName());
        assertEquals(UserConstantsTest.NEW_USER.getEmail(), user.getEmail());
    }

    @Test
    @DisplayName("Update user")
    void update_updateExistingUser_returnUpdatedUser() {
        userRepository.create(UserConstantsTest.VALID_USER_1);
        User user = userRepository.findById(1L).orElseThrow();
        user.setName("Updated");

        User updated = userRepository.update(user);

        assertEquals("Updated", updated.getName());
        assertEquals(UserConstantsTest.VALID_USER_1.getEmail(), updated.getEmail());
    }

    @Test
    @DisplayName("Delete user by id")
    void deleteById_deleteExistingUser_userNotFoundAfterDelete() {
        userRepository.create(UserConstantsTest.VALID_USER_1);

        userRepository.delete(UserConstantsTest.VALID_USER_1.getId());

        assertTrue(userRepository.findById(UserConstantsTest.VALID_USER_1.getId()).isEmpty());
    }
}
