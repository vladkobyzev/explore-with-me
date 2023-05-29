package ru.practicum.main_service.controllers.events;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.dto.events.EventFullDto;
import ru.practicum.main_service.dto.events.EventShortDto;
import ru.practicum.main_service.services.events.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/events")
@AllArgsConstructor
public class PublicEventController {
    private final EventService eventService;

    @GetMapping()
    public List<EventShortDto> getAllEvents(@RequestParam(name = "text", required = false) String text,
                                            @RequestParam(name = "categories", required = false) List<Long> categories,
                                            @RequestParam(name = "paid", required = false) Boolean paid,
                                            @RequestParam(value = "rangeStart", required = false) String rangeStart,
                                            @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
                                            @RequestParam(name = "onlyAvailable", defaultValue = "false") boolean onlyAvailable,
                                            @RequestParam(name = "sort", defaultValue = "EVENT_DATE") String sort,
                                            @RequestParam(name = "from", defaultValue = "0") Integer from,
                                            @RequestParam(name = "size", defaultValue = "10") Integer size,
                                            HttpServletRequest request) {
        return eventService.getAllEventsPublic(text, categories, paid, formatDate(rangeStart), formatDate(rangeEnd), onlyAvailable, sort, from, size, request.getRequestURI(), request.getRemoteAddr());
    }

    @GetMapping("/{id}")
    public EventFullDto getEventsById(@PathVariable long id, HttpServletRequest request) {
        return eventService.getEventByIdPublic(id, request.getRequestURI(), request.getRemoteAddr());
    }

    private LocalDateTime formatDate(String date) {
        if (date != null) {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(date, format);
        }
        return null;
    }
}
