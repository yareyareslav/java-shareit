package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.shared.error.ConflictException;
import ru.practicum.shareit.shared.error.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAll().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = findUserByIdOrThrow(id);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        checkEmailDuplicates(userDto.getEmail());
        User saved = userRepository.create(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(saved);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        User user = findUserByIdOrThrow(userDto.getId());
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank() && !userDto.getEmail().equalsIgnoreCase(user.getEmail())) {
            checkEmailDuplicates(userDto.getEmail());
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }
        return UserMapper.toUserDto(userRepository.update(user));
    }

    @Override
    public void deleteUser(Long id) {
        findUserByIdOrThrow(id);
        userRepository.delete(id);
    }

    private User findUserByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
    }

    private void checkEmailDuplicates(String email) {
        userRepository.findByEmail(email).ifPresent(existing -> {
            throw new ConflictException("Пользователь с email=" + email + " уже существует");
        });
    }
}
