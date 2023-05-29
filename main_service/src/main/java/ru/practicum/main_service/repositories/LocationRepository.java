package ru.practicum.main_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main_service.dto.events.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
}
