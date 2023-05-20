package ru.practicum.dto.events;

import lombok.Data;
import ru.practicum.dto.requests.ParticipationRequestDto;

import java.util.List;
@Data
public class EventRequestStatusUpdateResult {
    List<ParticipationRequestDto> confirmedRequests;
    List<ParticipationRequestDto> rejectedRequests;
}
