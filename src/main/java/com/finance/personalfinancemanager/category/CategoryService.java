package com.finance.personalfinancemanager.category;

import com.finance.personalfinancemanager.category.dto.CategoryRequest;
import com.finance.personalfinancemanager.category.dto.CategoryResponse;
import com.finance.personalfinancemanager.exception.BadRequestException;
import com.finance.personalfinancemanager.exception.ConflictException;
import com.finance.personalfinancemanager.exception.ForbiddenException;
import com.finance.personalfinancemanager.exception.ResourceNotFoundException;
import com.finance.personalfinancemanager.transaction.TransactionRepository;
import com.finance.personalfinancemanager.user.User;
import com.finance.personalfinancemanager.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Business rules for category management:
 * per-user uniqueness, default-category protection, referenced-category protection,
 * and multi-tenant isolation.
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    /** All categories visible to the user: defaults + own customs, stable order. */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories(Long userId) {
        return categoryRepository.findAllAccessibleByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Creates a custom category owned by the user.
     *
     * @throws ConflictException when the name collides with a default or own category (409)
     */
    @Transactional
    public CategoryResponse createCategory(Long userId, CategoryRequest request) {
        String name = request.name().trim();

        boolean clashesWithDefault = categoryRepository.existsByNameIgnoreCaseAndUserIsNull(name);
        boolean clashesWithOwn = categoryRepository.existsByNameIgnoreCaseAndUserId(name, userId);

        if (clashesWithDefault || clashesWithOwn) {
            throw new ConflictException("Category already exists");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Category category = Category.builder()
                .name(name)
                .type(request.type())
                .defaultCategory(false)
                .user(user)
                .build();

        return toResponse(categoryRepository.save(category));
    }

    /**
     * Deletes one of the user's custom categories.
     *
     * @throws BadRequestException      for defaults (400) or categories in use (400)
     * @throws ForbiddenException       when the name belongs to another user (403)
     * @throws ResourceNotFoundException when the name is unknown (404)
     */
    @Transactional
    public void deleteCategory(Long userId, String name) {
        String normalized = name.trim();

        if (categoryRepository.findByNameIgnoreCaseAndUserIsNull(normalized).isPresent()) {
            throw new BadRequestException("Default categories cannot be deleted");
        }

        Optional<Category> own = categoryRepository.findByNameIgnoreCaseAndUserId(normalized, userId);

        if (own.isPresent()) {
            Category category = own.get();

            if (transactionRepository.existsByCategoryIdAndUserId(category.getId(), userId)) {
                throw new BadRequestException("Category is referenced by transactions");
            }

            categoryRepository.delete(category);
            return;
        }

        if (categoryRepository.existsByNameIgnoreCase(normalized)) {
            throw new ForbiddenException("Access denied");
        }

        throw new ResourceNotFoundException("Category not found");
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getType(),
                !category.isDefaultCategory()
        );
    }
}