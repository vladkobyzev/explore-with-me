package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.models.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
        Optional<Category> findByName(String name);
        Boolean existsByName(String name);

    List<Category> findAllByIdIn(List<Long> ids);
}
