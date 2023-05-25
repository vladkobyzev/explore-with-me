package ru.practicum.controllers.events;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.events.*;
import ru.practicum.dto.requests.ParticipationRequestDto;
import ru.practicum.exceptions.BadRequest;
import ru.practicum.services.events.EventService;
import ru.practicum.services.requests.RequestService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@AllArgsConstructor
public class PrivateEventController {
    private final EventService eventService;
    private final RequestService requestService;

    @GetMapping
    public List<EventShortDto> getUserEventsPrivate(@PathVariable long userId,
                                                    @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                    @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return eventService.getUserEventsPrivate(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEventPrivate(@Valid @RequestBody NewEventDto newEventDto, @PathVariable long userId) {
        if (newEventDto.getEventDate() != null && newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequest("Event date and time cannot be earlier than two hours from now");
        }
        return eventService.addEventPrivate(newEventDto, userId);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByUserIdPrivate(@PathVariable long userId,
                                                @PathVariable long eventId,
                                                HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        return eventService.getEventDtoByIdPrivate(userId, eventId, ip);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByUserIdPrivate(@PathVariable long userId,
                                                   @PathVariable long eventId,
                                                   @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        if (updateEventUserRequest.getEventDate() != null && updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequest("Event date and time cannot be earlier than two hours from now");
        }
        return eventService.updateEventByUserIdPrivate(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequests(@PathVariable long userId,
                                                          @PathVariable long eventId) {
        return requestService.getEventRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateStatusRequest(@PathVariable long userId,
                                                              @PathVariable long eventId, @Valid @RequestBody(required = false) EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return requestService.updateStatusRequestsByUserId(userId, eventId, eventRequestStatusUpdateRequest);
    }
}
