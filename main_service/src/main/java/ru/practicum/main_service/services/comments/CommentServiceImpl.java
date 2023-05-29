package ru.practicum.main_service.services.comments;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.main_service.dto.comments.CommentFullDto;
import ru.practicum.main_service.dto.comments.CommentShortDto;
import ru.practicum.main_service.dto.comments.NewCommentDto;
import ru.practicum.main_service.exceptions.BadRequest;
import ru.practicum.main_service.exceptions.Conflict;
import ru.practicum.main_service.exceptions.EntityNotFound;
import ru.practicum.main_service.models.Comment;
import ru.practicum.main_service.models.Event;
import ru.practicum.main_service.models.Like;
import ru.practicum.main_service.models.User;
import ru.practicum.main_service.repositories.CommentRepository;
import ru.practicum.main_service.repositories.LikeRepository;
import ru.practicum.main_service.services.events.EventService;
import ru.practicum.main_service.services.users.UserService;
import ru.practicum.main_service.util.EventStatus;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventService eventService;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final LikeRepository likeRepository;


    @Override
    public List<CommentShortDto> getAllEventComments(long eventId, String sort, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Comment> comments = commentRepository.findAllComments(eventId, sort, pageRequest);
        return comments.stream().map(this::convertEntityToShortDto).collect(Collectors.toList());
    }

    @Override
    public List<CommentShortDto> getCommentReplies(long eventId, long comId, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        eventService.getEventById(eventId);
        List<Comment> comments = commentRepository.findAllCommentReplies(comId, pageRequest);
        return comments.stream().map(this::convertEntityToShortDto).collect(Collectors.toList());
    }

    public Comment getCommentById(long comId) {
        return commentRepository.findById(comId).orElseThrow(() ->
                new EntityNotFound("Comment with id=" + comId + " was not found"));
    }

    @Override
    public CommentFullDto createComment(NewCommentDto newCommentDto, long eventId, long userId) {
        User user = userService.getUserById(userId);
        Event event = eventService.getEventById(eventId);
        if (!event.getState().equals(EventStatus.PUBLISHED)) {
            throw new Conflict("Comment can only be created if event is published");
        }
        Comment comment = new Comment();
        comment.setText(newCommentDto.getText());
        comment.setAuthor(user);
        comment.setEvent(event);
        comment.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        return convertEntityToFullDto(commentRepository.save(comment));
    }

    @Override
    public CommentFullDto replyToComment(NewCommentDto newCommentDto, long eventId, long comId, long userId) {
        User user = userService.getUserById(userId);
        Event event = eventService.getEventById(eventId);
        Comment parentComment = getCommentById(comId);
        Comment comment = new Comment();
        comment.setText(newCommentDto.getText());
        comment.setAuthor(user);
        comment.setEvent(event);
        comment.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        comment.setParentComment(parentComment);
        return convertEntityToFullDto(commentRepository.save(comment));
    }

    @Override
    public CommentShortDto likeComment(long eventId, long comId, long userId, boolean like) {
        eventService.getEventById(eventId);
        User user = userService.getUserById(userId);
        Comment comment = commentRepository.findById(comId).orElseThrow(() ->
                new EntityNotFound("Comment with id=" + comId + " was not found"));
        if (likeRepository.existsByCommentAndUser(comment, user) && like) {
            throw new BadRequest("Attempt to put a like on an already liked comment");
        } else if (!likeRepository.existsByCommentAndUser(comment, user) && !like) {
            throw new BadRequest("Attempt to remove a like on a non-liked comment");
        }
        if (!likeRepository.existsByCommentAndUser(comment, user) && like) {
            Like commentLike = new Like();
            commentLike.setComment(comment);
            commentLike.setUser(user);
            likeRepository.save(commentLike);
            comment.increaseRating();
        } else if (likeRepository.existsByCommentAndUser(comment, user) && !like) {
            likeRepository.deleteByCommentAndUser(comment, user);
            comment.decreaseRating();
        }
        return convertEntityToShortDto(commentRepository.save(comment));
    }

    @Override
    public CommentFullDto updateComment(NewCommentDto commentDto, long eventId, long comId) {
        eventService.getEventById(eventId);
        Comment comment = getCommentById(comId);
        comment.setText(comment.getText());
        comment.setModified(true);
        comment.setUpdatedDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        return convertEntityToFullDto(commentRepository.save(comment));
    }

    @Override
    public void deleteComment(long eventId, long comId) {
        eventService.getEventById(eventId);
        commentRepository.deleteById(comId);
    }

    private CommentFullDto convertEntityToFullDto(Comment comment) {
        return modelMapper.map(comment, CommentFullDto.class);
    }

    private CommentShortDto convertEntityToShortDto(Comment comment) {
        return modelMapper.map(comment, CommentShortDto.class);
    }
}
