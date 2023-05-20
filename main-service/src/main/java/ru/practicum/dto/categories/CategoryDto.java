package ru.practicum.dto.categories;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CategoryDto {
    private int id;
    @NotNull
    private String name;
}
