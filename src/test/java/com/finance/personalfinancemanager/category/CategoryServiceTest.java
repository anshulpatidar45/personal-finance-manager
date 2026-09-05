package com.finance.personalfinancemanager.category;

import com.finance.personalfinancemanager.category.dto.CategoryRequest;
import com.finance.personalfinancemanager.exception.BadRequestException;
import com.finance.personalfinancemanager.exception.ConflictException;
import com.finance.personalfinancemanager.exception.ForbiddenException;
import com.finance.personalfinancemanager.exception.ResourceNotFoundException;
import com.finance.personalfinancemanager.transaction.TransactionRepository;
import com.finance.personalfinancemanager.user.User;
import com.finance.personalfinancemanager.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock private CategoryRepository categoryRepository;
    @Mock private TransactionRepository transactionRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private CategoryService categoryService;

    @Test
    void createCategory_Success() {
        CategoryRequest req = new CategoryRequest("Custom", CategoryType.INCOME);
        when(categoryRepository.existsByNameIgnoreCaseAndUserIsNull("Custom")).thenReturn(false);
        when(categoryRepository.existsByNameIgnoreCaseAndUserId("Custom", 1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(categoryRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var res = categoryService.createCategory(1L, req);
        assertEquals("Custom", res.name());
        assertTrue(res.isCustom());
    }

    @Test
    void createCategory_DuplicateDefault_ThrowsConflict() {
        CategoryRequest req = new CategoryRequest("Salary", CategoryType.INCOME);
        when(categoryRepository.existsByNameIgnoreCaseAndUserIsNull("Salary")).thenReturn(true);
        assertThrows(ConflictException.class, () -> categoryService.createCategory(1L, req));
    }

    @Test
    void deleteCategory_DefaultCategory_ThrowsBadRequest() {
        when(categoryRepository.findByNameIgnoreCaseAndUserIsNull("Salary")).thenReturn(Optional.of(new Category()));
        assertThrows(BadRequestException.class, () -> categoryService.deleteCategory(1L, "Salary"));
    }

    @Test
    void deleteCategory_ReferencedCategory_ThrowsBadRequest() {
        Category cat = Category.builder().id(10L).build();
        when(categoryRepository.findByNameIgnoreCaseAndUserIsNull("Custom")).thenReturn(Optional.empty());
        when(categoryRepository.findByNameIgnoreCaseAndUserId("Custom", 1L)).thenReturn(Optional.of(cat));
        when(transactionRepository.existsByCategoryIdAndUserId(10L, 1L)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> categoryService.deleteCategory(1L, "Custom"));
    }

    @Test
    void deleteCategory_OtherUsersCategory_ThrowsForbidden() {
        when(categoryRepository.findByNameIgnoreCaseAndUserIsNull("Other")).thenReturn(Optional.empty());
        when(categoryRepository.findByNameIgnoreCaseAndUserId("Other", 1L)).thenReturn(Optional.empty());
        when(categoryRepository.existsByNameIgnoreCase("Other")).thenReturn(true);

        assertThrows(ForbiddenException.class, () -> categoryService.deleteCategory(1L, "Other"));
    }

    @Test
    void deleteCategory_UnknownCategory_ThrowsNotFound() {
        when(categoryRepository.findByNameIgnoreCaseAndUserIsNull("Unknown")).thenReturn(Optional.empty());
        when(categoryRepository.findByNameIgnoreCaseAndUserId("Unknown", 1L)).thenReturn(Optional.empty());
        when(categoryRepository.existsByNameIgnoreCase("Unknown")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(1L, "Unknown"));
    }
}