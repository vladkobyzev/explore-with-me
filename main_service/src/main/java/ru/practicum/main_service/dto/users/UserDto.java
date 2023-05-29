package ru.practicum.main_service.dto.users;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class UserDto {
    private int id;
    @NotNull
    private String name;
    @NotNull
    @Email
    private String email;
}
