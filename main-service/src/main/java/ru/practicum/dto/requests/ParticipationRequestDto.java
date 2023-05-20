package ru.practicum.dto.requests;

import ru.practicum.util.EventStatus;

import java.time.LocalDateTime;

public class ParticipationRequestDto {
    private long id;
    private LocalDateTime created;
    private long event;
    private long requester;
    private EventStatus status;
}
