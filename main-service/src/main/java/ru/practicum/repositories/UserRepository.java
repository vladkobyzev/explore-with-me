package ru.practicum.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.models.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT u From User u " +
            "WHERE :ids IS NULL OR u.id IN :ids")
    List<User> findAllByIdIn(@Param("ids") List<Long> ids, Pageable pageable);
}
