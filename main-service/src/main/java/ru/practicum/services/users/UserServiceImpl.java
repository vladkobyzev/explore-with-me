package ru.practicum.services.users;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.users.NewUserRequest;
import ru.practicum.dto.users.UserDto;
import ru.practicum.exceptions.EntityNotFound;
import ru.practicum.models.User;
import ru.practicum.repositories.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        List<User> users;
        if(from == null && size == null) {
            users = userRepository.findAllById(ids);
        } else {
            PageRequest pageRequest = PageRequest.of(from / size, size);
            users = userRepository.findAllByIdIn(ids, pageRequest).toList();
        }
        return users.stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }
    @Override
    public User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFound("Category with id=" + userId + " was not found"));
    }

    @Override
    public UserDto addUser(NewUserRequest newUserRequest) {
        User user = convertDtoToEntity(newUserRequest);
        return convertEntityToDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }

    private UserDto convertEntityToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    private User convertDtoToEntity(NewUserRequest newUserRequest) {
        return modelMapper.map(newUserRequest, User.class);
    }
}
