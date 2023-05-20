package ru.practicum.dto.events;

import lombok.Data;
import ru.practicum.dto.categories.CategoryDto;
import ru.practicum.dto.events.Location;
import ru.practicum.util.EventStateAction;

import java.time.LocalDateTime;
@Data
public class UpdateEventAdminRequest {
    private String annotation;

    private CategoryDto category;
    private String description;
    private LocalDateTime eventDate;
    private Location location;
    private boolean paid;
    private int participantLimit;
    private LocalDateTime publishedOn;
    private boolean requestModeration;
    private EventStateAction stateAction;
    private String title;
}
