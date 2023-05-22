package ru.practicum.services.requests;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.dto.events.EventRequestStatusUpdateRequest;
import ru.practicum.dto.events.EventRequestStatusUpdateResult;
import ru.practicum.dto.requests.ParticipationRequestDto;
import ru.practicum.exceptions.Conflict;
import ru.practicum.exceptions.EntityNotFound;
import ru.practicum.models.Event;
import ru.practicum.models.Request;
import ru.practicum.models.User;
import ru.practicum.repositories.RequestRepository;
import ru.practicum.services.events.EventService;
import ru.practicum.services.users.UserService;
import ru.practicum.util.EventRequestStatus;
import ru.practicum.util.EventStatus;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final ModelMapper modelMapper;
    private final EventService eventService;
    private final UserService userService;


    @Override
    public ParticipationRequestDto addUserRequest(long userId, long eventId) {
        Request request = new Request();
        User user = userService.getUserById(userId);
        Event event = eventService.getEventById(eventId);
        if (event.getParticipantLimit() != 0 && requestRepository.countByEvent(event) >= event.getParticipantLimit()) {
            throw new Conflict("Exceeded the limit of participants");
        }
        if (!event.getState().equals(EventStatus.PUBLISHED)) {
            throw new Conflict("Adding a request to participate in an unpublished event");
        }
        if (requestRepository.existsByRequesterAndEvent(user, event)) {
            throw new Conflict("Repeat request from user=" + userId +" for event=" + eventId);
        }
        if (event.getInitiator().equals(user)) {
            throw new Conflict("Initiator cannot add a request to participate in his event");
        }
        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new Conflict("Exceeded the limit of participants");
        }
        request.setRequester(user);
        request.setEvent(event);
        request.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
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
        Event event = eventService.getEventById(eventId);
        if (event.getParticipantLimit() != 0 && requestRepository.countByEvent(event) >= event.getParticipantLimit()) {
            throw new Conflict("Exceeded the limit of participants");
        }
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
            result.setConfirmedRequests(Collections.emptyList());
            result.setRejectedRequests(Collections.emptyList());
            return result;
        }
        userService.getUserById(userId);

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        List<Request> requestsFromUpdateRequest = requestRepository.findAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds());


        for (Request request : requestsFromUpdateRequest) {
            if (request.getStatus() != EventRequestStatus.PENDING) {
                throw new Conflict("Status can be changed only for applications that are in the waiting state");
            }
            if (eventRequestStatusUpdateRequest.getStatus().equals(EventRequestStatus.CONFIRMED) &&
                    event.getConfirmedRequests() < event.getParticipantLimit()) {
                request.setStatus(eventRequestStatusUpdateRequest.getStatus());
                result.getConfirmedRequests().add(convertEntityToDto(request));
                int confirmedRequests = event.getConfirmedRequests();
                event.setConfirmedRequests(++confirmedRequests);
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

    private Request convertDtoToEntity(ParticipationRequestDto participationRequestDto) {
        return modelMapper.map(participationRequestDto, Request.class);
    }
}
