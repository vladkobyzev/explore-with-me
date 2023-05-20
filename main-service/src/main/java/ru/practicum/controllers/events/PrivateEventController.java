package ru.practicum.controllers.events;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.events.*;
import ru.practicum.dto.requests.ParticipationRequestDto;
import ru.practicum.exceptions.Forbidden;
import ru.practicum.services.events.EventService;
import ru.practicum.services.requests.RequestService;

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
                                                    @RequestParam(name = "from", required = false) Integer from,
                                                    @RequestParam(name = "size", required = false) Integer size) {
        return eventService.getUserEventsPrivate(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEventPrivate(@Valid @RequestBody NewEventDto newEventDto, @PathVariable long userId) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new Forbidden("Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: " +
                    newEventDto.getEventDate());
        }
        return eventService.addEventPrivate(newEventDto, userId);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByUserIdPrivate(@PathVariable long userId,
                                                @PathVariable long eventId) {
        return eventService.getEventDtoByIdPrivate(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByUserIdPrivate(@PathVariable long userId,
                                                   @PathVariable long eventId,
                                                   @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        return eventService.updateEventByUserIdPrivate(userId, eventId, updateEventAdminRequest);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequests(@PathVariable long userId,
                                                          @PathVariable long eventId) {
        return requestService.getEventRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateStatusRequest(@PathVariable long userId,
                                                  @PathVariable long eventId, @Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return requestService.updateStatusEventByUserId(userId, eventId, eventRequestStatusUpdateRequest);
    }
}
