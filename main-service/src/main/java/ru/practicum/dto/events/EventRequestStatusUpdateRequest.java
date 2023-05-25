package ru.practicum.dto.events;

import lombok.Data;
import ru.practicum.util.EventRequestStatus;

import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private EventRequestStatus status;
}
