package ru.practicum.dto.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.util.EventStateActionAdmin;
import ru.practicum.util.EventStateActionUser;

import java.time.LocalDateTime;
@Data
public class UpdateEventUserRequest {
    private String annotation;

    private Integer category;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime publishedOn;
    private Boolean requestModeration;
    private EventStateActionUser stateAction;
    private String title;
}
