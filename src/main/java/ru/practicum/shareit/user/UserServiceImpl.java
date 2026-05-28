package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.shared.error.ConflictException;
import ru.practicum.shareit.shared.error.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> users = userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .toList();
        log.info("Loaded {} users", users.size());
        return users;
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = findUserByIdOrThrow(id);
        log.info("Found user id={}", id);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        checkEmailDuplicates(userDto.getEmail());
        User saved = userRepository.save(UserMapper.toUser(userDto));
        log.info("Created user id={}, email={}", saved.getId(), saved.getEmail());
        return UserMapper.toUserDto(saved);
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = findUserByIdOrThrow(id);
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank() && !userDto.getEmail().equalsIgnoreCase(user.getEmail())) {
            checkEmailDuplicates(userDto.getEmail());
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }
        User updated = userRepository.save(user);
        log.info("Updated user id={}", updated.getId());
        return UserMapper.toUserDto(updated);
    }

    @Override
    public void deleteUser(Long id) {
        findUserByIdOrThrow(id);
        userRepository.deleteById(id);
        log.info("Deleted user id={}", id);
    }

    private User findUserByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found: id={}", id);
                    return new NotFoundException("Пользователь с id=" + id + " не найден");
                });
    }

    private void checkEmailDuplicates(String email) {
        userRepository.findByEmail(email).ifPresent(existing -> {
            log.warn("Duplicate email on save: email={}, existingUserId={}", email, existing.getId());
            throw new ConflictException("Пользователь с email=" + email + " уже существует");
        });
    }
}
