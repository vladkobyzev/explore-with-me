package ru.practicum.main_service.dto.compilations;

import lombok.Data;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
public class UpdateCompilationRequest {
    private List<Long> events = new ArrayList<>();
    private boolean pinned;
    @Size(max = 50)
    private String title;
}
