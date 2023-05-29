package ru.practicum.main_service.dto.events;

import lombok.Data;
import ru.practicum.main_service.util.EventRequestStatus;

import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private EventRequestStatus status;
}
