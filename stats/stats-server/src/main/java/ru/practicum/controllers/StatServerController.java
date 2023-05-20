package ru.practicum.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.services.StatService;

import java.util.List;

@RestController
@AllArgsConstructor
public class StatServerController {
    private final StatService statService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public HitDto saveStat(@RequestBody HitDto hitDto) {
        return statService.saveStat(hitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStat(@RequestParam(name = "start") String start,
                                      @RequestParam(name = "end") String end,
                                      @RequestParam(name = "uris", required = false) String[] uris,
                                      @RequestParam(name = "unique", defaultValue = "false") boolean unique) {

        return statService.getStat(start, end, uris, unique);
    }
}
