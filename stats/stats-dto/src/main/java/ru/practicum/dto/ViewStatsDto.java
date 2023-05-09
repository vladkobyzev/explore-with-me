package ru.practicum.dto;

import lombok.Data;

public interface ViewStatsDto {
    String getApp();
    String getUri();
    int getHits();
}
