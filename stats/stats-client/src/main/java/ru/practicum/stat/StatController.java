package ru.practicum.stat;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.HitDto;

import javax.validation.Valid;

@Controller
@AllArgsConstructor
public class StatController {
    private final StatClient statsClient;

    @PostMapping("/hit")
    public ResponseEntity<Object> saveStat(@Valid @RequestBody HitDto hitDto) {
        return statsClient.saveStat(hitDto);
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getStat(@RequestParam(name = "start") String start,
                                          @RequestParam(name = "end") String end,
                                          @RequestParam(name = "uris", required = false) String[] uris,
                                          @RequestParam(name = "unique", required = false) Boolean unique) {
        return statsClient.getStat(start, end, uris, unique);
    }
}
