package ru.practicum.main_service.dto.comments;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class NewCommentDto {
    @NotBlank
    @Size(min = 1, max = 450)
    private String text;
}
