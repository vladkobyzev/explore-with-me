package ru.practicum.main_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main_service.models.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {


}
