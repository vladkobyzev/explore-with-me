package ru.practicum.services;

import ru.practicum.dto.HitDto;
import ru.practicum.dto.ViewStatsDto;

import java.util.List;

public interface StatService {
    HitDto saveStat(HitDto hitDto);

    List<ViewStatsDto> getStat(String start, String end, String[] uris, boolean unique);
}
