package ru.practicum.main_service.dto.users;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserShortDto {
    private int id;
    @NotNull
    private String name;
}
