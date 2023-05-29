package ru.practicum.main_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main_service.models.Event;
import ru.practicum.main_service.models.View;

@Repository
public interface ViewRepository extends JpaRepository<View, String> {
    boolean existsByIpAndEvent(String ip, Event event);
}
