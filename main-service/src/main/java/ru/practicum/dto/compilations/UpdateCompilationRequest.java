package ru.practicum.dto.compilations;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class UpdateCompilationRequest {
    private List<Long> events;
    private boolean pinned;
    private String title;
}
