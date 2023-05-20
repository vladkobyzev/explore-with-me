package ru.practicum.dto.compilations;

import lombok.Data;
import ru.practicum.dto.events.EventShortDto;

import java.util.Set;

@Data
public class CompilationDto {
    private int id;
    private Set<EventShortDto> events;
    private boolean pinned;
    private String title;
}
