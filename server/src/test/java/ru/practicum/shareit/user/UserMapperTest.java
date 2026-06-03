package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    @Test
    @DisplayName("Map user to user dto")
    void toUserDto_mapUser_returnUserDto() {
        UserDto dto = UserMapper.toUserDto(UserConstantsTest.VALID_USER_1);

        assertEquals(UserConstantsTest.VALID_USER_1.getId(), dto.getId());
        assertEquals(UserConstantsTest.VALID_USER_1.getName(), dto.getName());
        assertEquals(UserConstantsTest.VALID_USER_1.getEmail(), dto.getEmail());
    }
}
