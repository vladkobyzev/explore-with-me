package ru.practicum.main_service.dto.compilations;

import lombok.Data;
import ru.practicum.main_service.dto.events.EventShortDto;

import java.util.Set;

@Data
public class CompilationDto {
    private int id;
    private Set<EventShortDto> events;
    private boolean pinned;
    private String title;
}
