package ru.practicum.main_service.services.users;

import ru.practicum.main_service.dto.users.NewUserRequest;
import ru.practicum.main_service.dto.users.UserDto;
import ru.practicum.main_service.models.User;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    User getUserById(long userId);

    UserDto addUser(NewUserRequest userDto);

    void deleteUser(long userId);
}
