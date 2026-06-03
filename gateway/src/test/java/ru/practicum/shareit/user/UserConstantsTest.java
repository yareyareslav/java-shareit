package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

public class UserConstantsTest {
    public static final UserDto NEW_USER_DTO = new UserDto(null, "New User", "new.user@gmail.com");
    public static final UserDto USER_UPDATE_NAME_DTO = new UserDto(null, "Updated Name", null);
    public static final UserDto INVALID_USER_DTO = new UserDto(null, "", "not-an-email");
}
