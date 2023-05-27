package ru.practicum.main_service.controllers.events;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.dto.events.EventFullDto;
import ru.practicum.main_service.dto.events.UpdateEventAdminRequest;
import ru.practicum.main_service.services.events.EventService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@AllArgsConstructor
public class AdminEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getAllEventsAdmin(@RequestParam(name = "users", required = false) List<Long> users,
                                                @RequestParam(name = "states", required = false) List<String> states,
                                                @RequestParam(name = "categories", required = false) List<Long> categories,
                                                @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                                @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                                @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return eventService.getAllEventsAdmin(users, states, categories, formatDate(rangeStart), formatDate(rangeEnd), from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest,
                                    @PathVariable long eventId) {
        return eventService.updateEventAdmin(updateEventAdminRequest, eventId);
    }

    private LocalDateTime formatDate(String date) {
        if (date != null) {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(date, format);
        }
        return null;
    }
}
