package ru.practicum.main_service.controllers.comments;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.dto.comments.CommentFullDto;
import ru.practicum.main_service.dto.comments.CommentShortDto;
import ru.practicum.main_service.dto.comments.NewCommentDto;
import ru.practicum.main_service.services.comments.CommentService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/events/{eventId}/comment")
@AllArgsConstructor
public class CommentController {
    private static final String USER_ID = "X-User-Id";
    private final CommentService commentService;
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public CommentFullDto createComment(@Valid @RequestBody NewCommentDto commentDto,
                                        @PathVariable long eventId,
                                        @RequestHeader(value = USER_ID) long userId) {
        return commentService.createComment(commentDto, eventId, userId);
    }

    @GetMapping()
    public List<CommentShortDto> getAllEventComments(@PathVariable long eventId,
                                                     @RequestParam(name = "sort", defaultValue = "MOST_POPULAR") String sort,
                                                     @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                     @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return commentService.getAllEventComments(eventId, sort, from, size);
    }

    @GetMapping("/{comId}")
    public List<CommentShortDto> getCommentReplies(@PathVariable long comId,
                                                    @PathVariable long eventId,
                                                    @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                    @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return commentService.getCommentReplies(eventId, comId, from, size);
    }

    @PostMapping("/{comId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentFullDto replyToComment(@PathVariable long comId, @Valid @RequestBody NewCommentDto commentDto,
                                                @PathVariable long eventId,
                                                @RequestHeader(value = USER_ID) long userId) {
        return commentService.replyToComment(commentDto, eventId, comId, userId);
    }

    @PatchMapping("/{comId}")
    public CommentFullDto updateComment(@Valid @RequestBody NewCommentDto commentDto, @PathVariable long comId, @PathVariable long eventId) {
        return commentService.updateComment(commentDto, eventId, comId);
    }

    @PatchMapping("/{comId}/like")
    public CommentShortDto likeComment(@PathVariable long comId,
                                       @PathVariable long eventId,
                                       @RequestHeader(value = USER_ID) long userId,
                                       @RequestParam(name = "like", required = false) boolean like) {
        return commentService.likeComment(eventId, comId, userId, like);
    }

    @DeleteMapping("/{comId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long eventId, @PathVariable long comId) {
        commentService.deleteComment(eventId, comId);
    }
}
