package ru.practicum.dto.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.util.EventStateActionAdmin;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class UpdateEventAdminRequest {
    @Size(min = 20, max = 2000)
    private String annotation;
    private int category;
    @Size(min = 20, max = 7000)
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime publishedOn;
    private Boolean requestModeration;
    private EventStateActionAdmin stateAction;
    @Size(min = 3, max = 120)
    private String title;
}
