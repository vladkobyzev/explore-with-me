package ru.practicum.services.requests;

import ru.practicum.dto.events.EventRequestStatusUpdateRequest;
import ru.practicum.dto.events.EventRequestStatusUpdateResult;
import ru.practicum.dto.requests.ParticipationRequestDto;
import ru.practicum.models.Request;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto addUserRequest(long userId, long eventId);

    List<ParticipationRequestDto> getUserRequests(long userId);

    ParticipationRequestDto cancelRequest(long requestId, long userId);

    Request getRequestById(long requestId);

    List<ParticipationRequestDto> getEventRequests(long userId, long eventId);

    EventRequestStatusUpdateResult updateStatusRequestsByUserId(long userId, long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);
}
