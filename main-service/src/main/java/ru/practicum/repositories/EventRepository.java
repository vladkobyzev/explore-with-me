package ru.practicum.repositories;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.models.Category;
import ru.practicum.models.Event;
import ru.practicum.models.User;
import ru.practicum.util.EventStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiator(User user, PageRequest pageRequest);
    List<Event> findAllByIdIn(List<Long> eventIds);
    @Query("SELECT e FROM Event e " +
            "WHERE (:initiator IS NULL OR e.initiator IN :initiator) " +
            "AND (:states IS NULL OR e.state IN :states) " +
            "AND (:category IS NULL OR e.category IN :category) " +
            "AND ((:rangeStart IS NULL AND :rangeEnd IS NULL) OR e.eventDate BETWEEN :rangeStart AND :rangeEnd)")
    List<Event> findAllEventsAdmin(@Param("initiator") List<User> initiator,
                                   @Param("states") List<EventStatus> states,
                                   @Param("category") List<Category> category,
                                   @Param("rangeStart") LocalDateTime rangeStart,
                                   @Param("rangeEnd") LocalDateTime rangeEnd,
                                   PageRequest pageRequest);


    @Query("SELECT e FROM Event e " +
            "WHERE (:text IS NULL OR (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%')))) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (:rangeStart IS NULL AND :rangeEnd IS NULL OR (e.eventDate > CURRENT_TIMESTAMP)) " +
            "AND (:onlyAvailable = false OR (e.confirmedRequests < e.participantLimit)) " +
            "ORDER BY CASE WHEN :sort = 'EVENT_DATE' THEN e.eventDate END ASC, " +
            "CASE WHEN :sort = 'VIEWS' THEN e.views END ASC")
    List<Event> findAllEventsPublic(@Param("text") String text,
                                    @Param("categories") List<Long> categories,
                                    @Param("paid") Boolean paid,
                                    @Param("rangeStart") LocalDateTime rangeStart,
                                    @Param("rangeEnd") LocalDateTime rangeEnd,
                                    @Param("onlyAvailable") Boolean onlyAvailable,
                                    @Param("sort") String sort, PageRequest pageRequest);

    Optional<Event> findByIdAndInitiator(Long id, User initiator);
}
