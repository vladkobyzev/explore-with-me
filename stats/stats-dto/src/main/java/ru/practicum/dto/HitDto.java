package ru.practicum.dto;

import lombok.Data;

@Data
public class HitDto {
    private int id;
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}
