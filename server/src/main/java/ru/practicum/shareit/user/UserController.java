package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.shared.dto.group.OnCreate;
import ru.practicum.shareit.shared.dto.group.OnUpdate;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("GET /users");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        log.info("GET /users/{}", id);
        return userService.getUserById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Validated(OnCreate.class) @RequestBody UserDto userDto) {
        log.info("POST /users email={}", userDto.getEmail());
        return userService.createUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(
            @PathVariable Long id,
            @Validated(OnUpdate.class) @RequestBody UserDto userDto
    ) {
        log.info("PATCH /users/{}", id);
        return userService.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable Long id) {
        log.info("DELETE /users/{}", id);
        userService.deleteUser(id);
    }
}
