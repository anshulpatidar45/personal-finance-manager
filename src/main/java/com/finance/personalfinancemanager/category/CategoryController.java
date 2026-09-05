package com.finance.personalfinancemanager.category;

import com.finance.personalfinancemanager.category.dto.CategoryRequest;
import com.finance.personalfinancemanager.category.dto.CategoryResponse;
import com.finance.personalfinancemanager.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/** Category endpoints; authentication enforced by SecurityConfig. */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /** GET /api/categories -> 200 */
    @GetMapping
    public Map<String, List<CategoryResponse>> getCategories() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Map.of("categories", categoryService.getAllCategories(userId));
    }

    /** POST /api/categories -> 201 */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(@Valid @RequestBody CategoryRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return categoryService.createCategory(userId, request);
    }

    /** DELETE /api/categories/{name} -> 200 */
    @DeleteMapping("/{name}")
    public Map<String, String> deleteCategory(@PathVariable String name) {
        Long userId = SecurityUtils.getCurrentUserId();
        categoryService.deleteCategory(userId, name);
        return Map.of("message", "Category deleted successfully");
    }
}