package ru.practicum.dto.users;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
@Data
public class NewUserRequest {
    @NotNull
    private String name;
    @NotNull
    @Email
    private String email;
}
