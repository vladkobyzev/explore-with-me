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
import ru.practicum.repositories.RequestRepository;
import ru.practicum.services.events.EventService;
import ru.practicum.services.users.UserService;
import ru.practicum.util.EventRequestStatus;
import ru.practicum.util.EventStatus;

import java.time.LocalDateTime;
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
        request.setRequester(userService.getUserById(userId));
        request.setEvent(eventService.getEventById(eventId));
        request.setCreated(LocalDateTime.now());
        return convertEntityToDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(long userId) {
        return requestRepository.findAllByRequester(userId).stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto cancelRequest(long requestId, long userId) {
        Request request = getRequestById(requestId);
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
        eventService.getEventById(eventId);
        return requestRepository.findAllByEvent(eventId).stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult updateStatusEventByUserId(long userId, long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        userService.getUserById(userId);
        Event event = eventService.getEventById(eventId);
        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new Conflict("Request limit reached");
        }
        if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
            EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
            result.setConfirmedRequests(Collections.emptyList());
            result.setRejectedRequests(Collections.emptyList());
            return result;
        }

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        List<Request> requestsFromUpdateRequest = requestRepository.findAllByEventIn(eventRequestStatusUpdateRequest.getRequestIds());


        for (Request request : requestsFromUpdateRequest) {
            if (request.getStatus() != EventRequestStatus.PENDING) {
                throw new Conflict("Status can be changed only for applications that are in the waiting state");
            }


            if (event.getConfirmedRequests() < event.getParticipantLimit()) {
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
        return modelMapper.map(request, ParticipationRequestDto.class);
    }

    private Request convertDtoToEntity(ParticipationRequestDto participationRequestDto) {
        return modelMapper.map(participationRequestDto, Request.class);
    }
}
