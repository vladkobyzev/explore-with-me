package ru.practicum.main_service.services.requests;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.main_service.dto.events.EventRequestStatusUpdateRequest;
import ru.practicum.main_service.dto.events.EventRequestStatusUpdateResult;
import ru.practicum.main_service.dto.requests.ParticipationRequestDto;
import ru.practicum.main_service.exceptions.Conflict;
import ru.practicum.main_service.exceptions.EntityNotFound;
import ru.practicum.main_service.models.Event;
import ru.practicum.main_service.models.Request;
import ru.practicum.main_service.models.User;
import ru.practicum.main_service.repositories.RequestRepository;
import ru.practicum.main_service.services.events.EventService;
import ru.practicum.main_service.services.users.UserService;
import ru.practicum.main_service.util.EventRequestStatus;
import ru.practicum.main_service.util.EventStatus;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventService eventService;
    private final UserService userService;


    @Override
    public ParticipationRequestDto addUserRequest(long userId, long eventId) {
        User user = userService.getUserById(userId);
        Event event = eventService.getEventById(eventId);
        Request request = new Request();
        isValidAddRequest(event, user, userId, eventId);
        setUserRequest(event, user, request);
        return convertEntityToDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(long userId) {
        return requestRepository.findAllByRequester_Id(userId).stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto cancelRequest(long requestId, long userId) {
        Request request = getRequestById(requestId);
        request.setStatus(EventRequestStatus.CANCELED);
        requestRepository.delete(request);
        return convertEntityToDto(request);
    }

    @Override
    public Request getRequestById(long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() ->
                new EntityNotFound("Category with id=" + requestId + " was not found"));
    }

    @Override
    public List<ParticipationRequestDto> getEventRequests(long userId, long eventId) {
        userService.getUserById(userId);
        Event event = eventService.getEventById(eventId);
        return requestRepository.findAllByEvent(event).stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult updateStatusRequestsByUserId(long userId, long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        userService.getUserById(userId);
        Event event = eventService.getEventById(eventId);
        if (event.getParticipantLimit() != 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new Conflict("Exceeded the limit of participants");
        }
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        List<Request> requestsFromUpdateRequest = requestRepository.findAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds());
        for (Request request : requestsFromUpdateRequest) {
            if (request.getStatus() == EventRequestStatus.CANCELED ||
                    (request.getStatus() == EventRequestStatus.CONFIRMED && eventRequestStatusUpdateRequest.getStatus() == EventRequestStatus.CANCELED)) {
                throw new Conflict("Status can be changed only for applications that are in the waiting state");
            }
            if (eventRequestStatusUpdateRequest.getStatus().equals(EventRequestStatus.CONFIRMED) &&
                    event.getConfirmedRequests() < event.getParticipantLimit()) {
                request.setStatus(eventRequestStatusUpdateRequest.getStatus());
                result.getConfirmedRequests().add(convertEntityToDto(request));
                event.incrementConfirmedRequests();
            } else {
                request.setStatus(EventRequestStatus.REJECTED);
                result.getRejectedRequests().add(convertEntityToDto(request));
            }
        }
        requestRepository.saveAll(requestsFromUpdateRequest);
        return result;
    }

    private ParticipationRequestDto convertEntityToDto(Request request) {
        ParticipationRequestDto participationRequestDto = new ParticipationRequestDto();
        participationRequestDto.setId(request.getId());
        participationRequestDto.setCreated(request.getCreated());
        participationRequestDto.setEvent(request.getEvent().getId());
        participationRequestDto.setRequester(request.getRequester().getId());
        participationRequestDto.setStatus(request.getStatus());
        return participationRequestDto;
    }

    private void isValidAddRequest(Event event, User user, long userId, long eventId) {
        if (event.getParticipantLimit() != 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new Conflict("Exceeded the limit of participants");
        }
        if (!event.getState().equals(EventStatus.PUBLISHED)) {
            throw new Conflict("Adding a request to participate in an unpublished event");
        }
        if (requestRepository.existsByRequesterAndEvent(user, event)) {
            throw new Conflict("Repeat request from user=" + userId + " for event=" + eventId);
        }
        if (event.getInitiator().equals(user)) {
            throw new Conflict("Initiator cannot add a request to participate in his event");
        }
    }

    private void setUserRequest(Event event, User user, Request request) {
        if (event.getParticipantLimit() == 0) {
            request.setStatus(EventRequestStatus.CONFIRMED);
        }
        if (!event.getRequestModeration()) {
            request.setStatus(EventRequestStatus.CONFIRMED);
            event.incrementConfirmedRequests();
        }
        request.setRequester(user);
        request.setEvent(event);
        request.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    }
}
