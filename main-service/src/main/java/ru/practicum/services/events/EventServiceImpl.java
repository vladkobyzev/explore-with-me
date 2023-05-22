package ru.practicum.services.events;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.events.*;
import ru.practicum.exceptions.Conflict;
import ru.practicum.exceptions.EntityNotFound;
import ru.practicum.models.Category;
import ru.practicum.models.Event;
import ru.practicum.models.User;
import ru.practicum.repositories.EventRepository;
import ru.practicum.repositories.LocationRepository;
import ru.practicum.services.categories.CategoryService;
import ru.practicum.services.users.UserService;
import ru.practicum.util.EventStateActionAdmin;
import ru.practicum.util.EventStateActionUser;
import ru.practicum.util.EventStatus;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    public EventFullDto updateEventByUserIdPrivate(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest) {
        User user = userService.getUserById(userId);
        Event event = eventRepository.findByIdAndInitiator(eventId, user).orElseThrow(() ->
                new EntityNotFound("Event with id=" + eventId + " was not found"));
        if (event.getState() != EventStatus.CANCELED && event.getState() != EventStatus.PENDING) {
            throw new Conflict("Event can only be modified if it is canceled or in moderation state");
        }
        if (updateEventUserRequest.getEventDate() != null && updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new Conflict("Event date and time cannot be earlier than two hours from now");
        }
        modelMapper.map(updateEventUserRequest, event);
        if (updateEventUserRequest.getStateAction().equals(EventStateActionUser.CANCEL_REVIEW)) {
            event.setState(EventStatus.CANCELED);
        } else if (updateEventUserRequest.getStateAction().equals(EventStateActionUser.SEND_TO_REVIEW)) {
            event.setState(EventStatus.PENDING);
        }
        return convertEntityToDto(eventRepository.save(event));
    }

    @Override
    public List<Event> getAllEventsIn(List<Long> eventIds) {
        return eventRepository.findAllByIdIn(eventIds);
    }


    @Override
    public List<EventFullDto> getAllEventsAdmin(List<Long> usersIds, List<String> states, List<Long> categoriesIds, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<User> users = (usersIds == null)  ? null :userService.getUsersIn(usersIds);
        List<Category> categories = (categoriesIds == null) ? null : categoryService.getAllCategoriesIn(categoriesIds);
        List<EventStatus> eventStatuses = (states == null) ? null :states.stream().map(EventStatus::valueOf).collect(Collectors.toList());
        List<Event> events = eventRepository.findAllEventsAdmin(users, eventStatuses, categories, rangeStart, rangeEnd, pageRequest);
        return events.stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEventAdmin(UpdateEventAdminRequest updateEventAdminRequest, long eventId) {
        Event event = getEventById(eventId);
            if (updateEventAdminRequest.getEventDate() != null && updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new Conflict("The start date of the event to be modified must be no earlier than an hour from the date of publication");
            }

        if (updateEventAdminRequest.getStateAction().equals(EventStateActionAdmin.REJECT_EVENT) && event.getState().equals(EventStatus.PUBLISHED)) {
            throw new Conflict("An event can be rejected only if it has not been published yet");
        }
        if (!event.getState().equals(EventStatus.PENDING)) {
            throw new Conflict("You can only change canceled events or events in the state of waiting for moderation");
        }
        modelMapper.map(updateEventAdminRequest, event);
        if (updateEventAdminRequest.getStateAction().equals(EventStateActionAdmin.PUBLISH_EVENT)) {
            event.setState(EventStatus.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        } else if (updateEventAdminRequest.getStateAction().equals(EventStateActionAdmin.REJECT_EVENT)) {
            event.setState(EventStatus.CANCELED);
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
