package ru.practicum.services.events;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.events.*;
import ru.practicum.exceptions.Conflict;
import ru.practicum.exceptions.EntityNotFound;
import ru.practicum.models.Event;
import ru.practicum.models.User;
import ru.practicum.repositories.EventRepository;
import ru.practicum.repositories.LocationRepository;
import ru.practicum.services.categories.CategoryService;
import ru.practicum.services.users.UserService;
import ru.practicum.util.EventStateAction;
import ru.practicum.util.EventStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    private final ModelMapper modelMapper;

    private final EventRepository eventRepository;
    private final UserService userService;
    private final LocationRepository locationRepository;
    private final CategoryService categoryService;

    @Override
    public List<EventShortDto> getUserEventsPrivate(long userId, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return eventRepository.findAllByInitiator(userService.getUserById(userId), pageRequest).stream()
                .map(this::convertEntityToShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto addEventPrivate(NewEventDto newEventDto, long userId) {
        Event event = convertDtoToEntity(newEventDto);
        event.setCategory(categoryService.getCategoryById(newEventDto.getCategory()));
        event.setCreatedOn(LocalDateTime.now());
        event.setInitiator(userService.getUserById(userId));
        Location location = locationRepository.save(newEventDto.getLocation());
        event.setLocation(location);
        return convertEntityToDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getEventDtoByIdPrivate(long userId, long eventId) {
        User user = userService.getUserById(userId);
        Event event = eventRepository.findByIdAndInitiator(eventId, user).orElseThrow(() ->
                new EntityNotFound("Event with id=" + eventId + " was not found"));
        return convertEntityToDto(event);
    }

    @Override
    public Event getEventById(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFound("Event with id=" + eventId + " was not found"));
    }


    @Override
    public EventFullDto updateEventByUserIdPrivate(long userId, long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        User user = userService.getUserById(userId);
        Event event = eventRepository.findByIdAndInitiator(eventId, user).orElseThrow(() ->
                new EntityNotFound("Event with id=" + eventId + " was not found"));
        if (event.getState() != EventStatus.CANCELED && event.getState() != EventStatus.PENDING) {
            throw new Conflict("Event can only be modified if it is canceled or in moderation state");
        }
        if (updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new Conflict("Event date and time cannot be earlier than two hours from now");
        }
        BeanUtils.copyProperties(updateEventAdminRequest, event);
        return convertEntityToDto(eventRepository.save(event));
    }

    @Override
    public List<Event> getAllEventsIn(List<Long> eventIds) {
        return eventRepository.findAllByIdIn(eventIds);
    }


    @Override
    public List<EventFullDto> getAllEventsAdmin(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllEventsAdmin(users, states, categories, rangeStart, rangeEnd, pageRequest);
        return events.stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEventAdmin(UpdateEventAdminRequest updateEventAdminRequest, long eventId) {
        Event event = getEventById(eventId);
        if (updateEventAdminRequest.getEventDate() != null && event.getPublishedOn() != null) {
            if (updateEventAdminRequest.getEventDate().isBefore(event.getPublishedOn().plusHours(1))) {
                throw new Conflict("The start date of the event to be modified must be no earlier than an hour from the date of publication");
            }
        }
        if (updateEventAdminRequest.getStateAction().equals(EventStateAction.CANCEL_REVIEW) && event.getState().equals(EventStatus.PUBLISHED)) {
            throw new Conflict("An event can be rejected only if it has not been published yet");
        }
        BeanUtils.copyProperties(updateEventAdminRequest, event);
        if (!event.getState().equals(EventStatus.PENDING)) {
            throw new Conflict("You can only change canceled events or events in the state of waiting for moderation");

        }
        return convertEntityToDto(eventRepository.save(event));
    }


    @Override
    public List<EventShortDto> getAllEventsPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllEventsPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, pageRequest);
        return events.stream()
                .map(this::convertEntityToShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventByIdPublic(long eventId, String requestURI, String remoteAddr) {
        Event event = getEventById(eventId);
        if (!event.getState().equals(EventStatus.PUBLISHED)) {
            throw new EntityNotFound("Event with id=" + eventId + " was not found");
        }
        //statClient.saveStat(getHitDto(requestURI, remoteAddr));
        return convertEntityToDto(event);
    }


    private HitDto getHitDto(String requestURI, String remoteAddr) {
        HitDto hitDto = new HitDto();
        hitDto.setApp("ewm-main-service");
        hitDto.setUri(requestURI);
        hitDto.setIp(remoteAddr);
        hitDto.setTimestamp(LocalDateTime.now().toString());
        return hitDto;
    }

    private EventFullDto convertEntityToDto(Event event) {
        return modelMapper.map(event, EventFullDto.class);
    }

    private EventShortDto convertEntityToShortDto(Event event) {
        return modelMapper.map(event, EventShortDto.class);
    }

    private Event convertDtoToEntity(NewEventDto newEventDto) {
        return modelMapper.map(newEventDto, Event.class);
    }

}
