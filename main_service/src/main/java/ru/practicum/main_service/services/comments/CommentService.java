package ru.practicum.main_service.services.comments;

import ru.practicum.main_service.dto.comments.CommentFullDto;
import ru.practicum.main_service.dto.comments.CommentShortDto;
import ru.practicum.main_service.dto.comments.NewCommentDto;

import java.util.List;

public interface CommentService {

    CommentFullDto createComment(NewCommentDto commentDto, long eventId, long userId);

    List<CommentShortDto> getAllEventComments(long eventId, String sort, Integer from, Integer size);

    CommentShortDto likeComment(long eventId, long comId, long userId, boolean like);

    CommentFullDto updateComment(NewCommentDto commentDto, long eventId, long comId);

    void deleteComment(long eventId, long comId);

    List<CommentShortDto> getCommentReplies(long eventId, long comId, Integer from, Integer size);

    CommentFullDto replyToComment(NewCommentDto commentDto, long eventId, long comId, long userId);
}
