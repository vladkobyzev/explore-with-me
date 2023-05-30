package ru.practicum.main_service.dto.comments;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentShortDto {
    private Long id;
    private String text;
    private Long eventId;
    private String authorName;
    private Long rating;
    private Long parentComment;
    private LocalDateTime created;
}
