package ru.practicum.main_service.services.categories;

import ru.practicum.main_service.dto.categories.CategoryDto;
import ru.practicum.main_service.dto.categories.NewCategoryDto;
import ru.practicum.main_service.models.Category;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(NewCategoryDto newCategoryDto);

    void deleteCategory(long catId);

    boolean existsById(long catId);

    CategoryDto updateCategory(NewCategoryDto newCategoryDto, long catId);

    List<CategoryDto> getAllCategories(Integer from, Integer size);

    CategoryDto getCategoryDtoById(long catId);

    Category getCategoryById(long catId);
}
