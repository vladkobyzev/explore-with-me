package ru.practicum.main_service.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.main_service.models.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE c.event.id = :event " +
            "AND c.parentComment IS NULL " +
            "ORDER BY CASE WHEN :sort = 'MOST_POPULAR' THEN c.rating END DESC, " +
            "CASE WHEN :sort = 'NEW' THEN c.created END ASC")
    List<Comment> findAllComments(@Param("event") Long event, @Param("sort") String sort, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.parentComment = :comId")
    List<Comment> findAllCommentReplies(@Param("comId") Long comId, Pageable pageable);

}
