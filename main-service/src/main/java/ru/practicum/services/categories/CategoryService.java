package ru.practicum.services.categories;

import ru.practicum.dto.categories.CategoryDto;
import ru.practicum.dto.categories.NewCategoryDto;
import ru.practicum.models.Category;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(NewCategoryDto newCategoryDto);

    void deleteCategory(long catId);

    boolean existsById(long catId);

    CategoryDto updateCategory(NewCategoryDto newCategoryDto, long catId);

    List<CategoryDto> getAllCategories(Integer from, Integer size);

    CategoryDto getCategoryDtoById(long catId);

    List<Category> getAllCategoriesIn(List<Long> ids);

    Category getCategoryById(long catId);
}
