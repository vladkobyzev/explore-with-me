package ru.practicum.dto.requests;

import lombok.Data;
import ru.practicum.util.EventRequestStatus;

import java.time.LocalDateTime;

@Data
public class ParticipationRequestDto {
    private long id;
    private LocalDateTime created;
    private long event;
    private long requester;
    private EventRequestStatus status;
}
