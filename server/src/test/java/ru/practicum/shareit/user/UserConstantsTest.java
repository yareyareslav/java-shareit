package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

public class UserConstantsTest {
    public static final String NON_EXISTING_EMAIL = "non-existing-email@gmail.com";

    public static final User VALID_USER_1 = new User(1L, "Valid User 1", "valid.user.1@gmail.com");
    public static final User VALID_USER_2 = new User(2L, "Valid User 2", "valid.user.2@gmail.com");
    public static final User NEW_USER = new User(null, "New User", "new.user@gmail.com");
    public static final UserDto NEW_USER_DTO = new UserDto(null, NEW_USER.getName(), NEW_USER.getEmail());

    public static final UserDto USER_UPDATE_NAME_DTO = new UserDto(null, "Updated Name", null);
    public static final UserDto USER_UPDATE_EMAIL_DTO = new UserDto(null, null, "updated.email@gmail.com");

    public static final Long NON_EXISTING_USER_ID = 999L;
}
