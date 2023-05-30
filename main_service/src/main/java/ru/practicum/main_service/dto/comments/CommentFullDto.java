package ru.practicum.main_service.dto.comments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentFullDto {
    private long id;

    private String text;

    private String authorName;

    private long eventId;

    private boolean modified;

    private LocalDateTime updatedDate;

    private long rating;

    private long parentComment;

    private LocalDateTime created;
}
