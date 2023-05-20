package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.models.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequester(Long userId);
    List<Request> findAllByEvent(Long eventId);

    List<Request> findAllByEventIn(List<Long> eventIds);

}
