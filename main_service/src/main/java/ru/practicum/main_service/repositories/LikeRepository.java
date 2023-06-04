package ru.practicum.main_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main_service.models.Comment;
import ru.practicum.main_service.models.Like;
import ru.practicum.main_service.models.User;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByCommentAndUser(Comment comment, User user);

    void deleteByCommentAndUser(Comment comment, User user);
}
