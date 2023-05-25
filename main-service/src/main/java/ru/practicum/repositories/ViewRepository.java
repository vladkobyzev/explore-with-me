package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.models.Event;
import ru.practicum.models.View;
@Repository
public interface ViewRepository extends JpaRepository<View, String> {
    boolean existsByIpAndEvent(String ip, Event event);
}
