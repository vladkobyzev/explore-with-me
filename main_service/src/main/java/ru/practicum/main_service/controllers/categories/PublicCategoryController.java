package ru.practicum.main_service.controllers.categories;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.dto.categories.CategoryDto;
import ru.practicum.main_service.services.categories.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@AllArgsConstructor
public class PublicCategoryController {
    private final CategoryService categoryService;

    @GetMapping()
    public List<CategoryDto> getAllCategories(@RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return categoryService.getAllCategories(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(@PathVariable long catId) {
        return categoryService.getCategoryDtoById(catId);
    }
}
