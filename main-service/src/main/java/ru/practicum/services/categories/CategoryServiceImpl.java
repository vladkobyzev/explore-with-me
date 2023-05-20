package ru.practicum.services.categories;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.categories.CategoryDto;
import ru.practicum.dto.categories.NewCategoryDto;
import ru.practicum.exceptions.Conflict;
import ru.practicum.exceptions.EntityNotFound;
import ru.practicum.models.Category;
import ru.practicum.repositories.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        Category category = convertDtoToEntity(newCategoryDto);
        return convertEntityToDto(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(long catId) {
        categoryRepository.deleteById(catId);
    }
    @Override
    public boolean existsById(long catId) {
        return categoryRepository.existsById(catId);
    }

    @Override
    public CategoryDto updateCategory(NewCategoryDto newCategoryDto, long catId) {
        Category category = getCategoryById(catId);
        category.setName(newCategoryDto.getName());
        return convertEntityToDto(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return categoryRepository.findAll(pageRequest).stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }


    @Override
    public CategoryDto getCategoryDtoById(long catId) {
        return convertEntityToDto(getCategoryById(catId));
    }

    @Override
    public Category getCategoryById(long catId) {
        return categoryRepository.findById(catId).orElseThrow(() ->
                new EntityNotFound("Category with id=" + catId + " was not found"));
    }


    private CategoryDto convertEntityToDto(Category category) {
        return modelMapper.map(category, CategoryDto.class);
    }

    private Category convertDtoToEntity(NewCategoryDto newCategoryDto) {
        return modelMapper.map(newCategoryDto, Category.class);
    }
}
