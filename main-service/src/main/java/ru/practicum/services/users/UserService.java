package ru.practicum.services.users;

import ru.practicum.dto.users.NewUserRequest;
import ru.practicum.dto.users.UserDto;
import ru.practicum.models.User;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    User getUserById(long userId);

    UserDto addUser(NewUserRequest userDto);

    List<User> getUsersIn(List<Long> ids);

    void deleteUser(long userId);
}
