package ru.practicum.ewm.categories.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.categories.dto.CategoryDTO;
import ru.practicum.ewm.categories.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDTO create(@Valid @RequestBody CategoryDTO categoryDTO) {
        return categoryService.create(categoryDTO);
    }

    @PatchMapping("/{catId}")
    public CategoryDTO update(@Positive @PathVariable Long catId,
                              @Valid @RequestBody CategoryDTO categoryDTO) {
        return categoryService.update(catId, categoryDTO);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Positive @PathVariable Long catId) {
        categoryService.delete(catId);
    }
}