package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.models.Event;
import ru.practicum.models.Request;
import ru.practicum.models.User;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequester_Id(Long userId);
    List<Request> findAllByEvent(Event event);

    List<Request> findAllByIdIn(List<Long> requestsIds);
    boolean existsByRequesterAndEvent(User requester, Event event);

    Long countByEvent(Event event);
}
