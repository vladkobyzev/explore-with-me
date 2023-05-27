package ru.practicum.main_service.services.requests;

import ru.practicum.main_service.dto.events.EventRequestStatusUpdateRequest;
import ru.practicum.main_service.dto.events.EventRequestStatusUpdateResult;
import ru.practicum.main_service.dto.requests.ParticipationRequestDto;
import ru.practicum.main_service.models.Request;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto addUserRequest(long userId, long eventId);

    List<ParticipationRequestDto> getUserRequests(long userId);

    ParticipationRequestDto cancelRequest(long requestId, long userId);

    Request getRequestById(long requestId);

    List<ParticipationRequestDto> getEventRequests(long userId, long eventId);

    EventRequestStatusUpdateResult updateStatusRequestsByUserId(long userId, long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);
}
