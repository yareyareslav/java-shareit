package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.shared.error.ConflictException;
import ru.practicum.shareit.shared.error.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    @DisplayName("Get all users")
    void getAllUsers_returnAllUserDtos() {
        when(userRepository.getAll()).thenReturn(List.of(UserConstantsTest.VALID_USER_1));

        List<UserDto> users = userService.getAllUsers();

        assertEquals(1, users.size());
        assertEquals(UserConstantsTest.VALID_USER_1.getId(), users.getFirst().getId());
    }

    @Test
    @DisplayName("Get user by id")
    void getUserById_existingUser_returnUserDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(UserConstantsTest.VALID_USER_1));

        UserDto user = userService.getUserById(1L);

        assertEquals(UserConstantsTest.VALID_USER_1.getEmail(), user.getEmail());
    }

    @Test
    @DisplayName("Get non-existing user by id")
    void getUserById_nonExistingUser_throwNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    @DisplayName("Create user")
    void createUser_newUser_returnUserDto() {
        when(userRepository.findByEmail(UserConstantsTest.NEW_USER_DTO.getEmail())).thenReturn(Optional.empty());
        when(userRepository.create(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return new User(3L, user.getName(), user.getEmail());
        });

        UserDto user = userService.createUser(UserConstantsTest.NEW_USER_DTO);

        assertEquals(3L, user.getId());
        verify(userRepository).create(any(User.class));
    }

    @Test
    @DisplayName("Create user with duplicate email")
    void createUser_duplicateEmail_throwConflictException() {
        when(userRepository.findByEmail(UserConstantsTest.NEW_USER_DTO.getEmail()))
                .thenReturn(Optional.of(UserConstantsTest.VALID_USER_1));

        assertThrows(ConflictException.class, () -> userService.createUser(UserConstantsTest.NEW_USER_DTO));
        verify(userRepository, never()).create(any());
    }

    @Test
    @DisplayName("Update user name")
    void updateUser_existingUser_updateName() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(UserConstantsTest.VALID_USER_1));
        when(userRepository.update(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto user = userService.updateUser(UserConstantsTest.USER_UPDATE_NAME_DTO);

        assertEquals("Updated Name", user.getName());
        assertEquals(UserConstantsTest.VALID_USER_1.getEmail(), user.getEmail());
    }

    @Test
    @DisplayName("Update non-existing user")
    void updateUser_nonExistingUser_throwNotFoundException() {
        when(userRepository.findById(UserConstantsTest.USER_UPDATE_NAME_NONEXISTING_DTO.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.updateUser(UserConstantsTest.USER_UPDATE_NAME_NONEXISTING_DTO));
    }

    @Test
    @DisplayName("Update user email to duplicate")
    void updateUser_duplicateEmail_throwConflictException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(UserConstantsTest.VALID_USER_1));
        when(userRepository.findByEmail(UserConstantsTest.USER_UPDATE_EMAIL_DTO.getEmail()))
                .thenReturn(Optional.of(UserConstantsTest.VALID_USER_2));

        assertThrows(ConflictException.class,
                () -> userService.updateUser(UserConstantsTest.USER_UPDATE_EMAIL_DTO));
    }

    @Test
    @DisplayName("Delete user")
    void deleteUser_existingUser_deleteFromRepository() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(UserConstantsTest.VALID_USER_1));

        userService.deleteUser(1L);

        verify(userRepository).delete(1L);
    }

    @Test
    @DisplayName("Delete non-existing user")
    void deleteUser_nonExistingUser_throwNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.deleteUser(99L));
        verify(userRepository, never()).delete(anyLong());
    }
}
