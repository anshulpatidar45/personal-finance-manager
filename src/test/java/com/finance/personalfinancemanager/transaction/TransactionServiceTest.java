package com.finance.personalfinancemanager.transaction;

import com.finance.personalfinancemanager.category.CategoryRepository;
import com.finance.personalfinancemanager.exception.ForbiddenException;
import com.finance.personalfinancemanager.exception.ResourceNotFoundException;
import com.finance.personalfinancemanager.transaction.dto.TransactionUpdateRequest;
import com.finance.personalfinancemanager.user.User;
import com.finance.personalfinancemanager.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private TransactionService transactionService;

    @Test
    void updateTransaction_NotOwner_ThrowsForbidden() {
        User owner = User.builder().id(2L).build();
        Transaction tx = Transaction.builder().id(1L).user(owner).build();

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(tx));
        TransactionUpdateRequest req = new TransactionUpdateRequest(new BigDecimal("100"), "Food", "desc");

        assertThrows(ForbiddenException.class, () -> transactionService.updateTransaction(1L, 1L, req));
    }

    @Test
    void deleteTransaction_NotOwner_ThrowsForbidden() {
        User owner = User.builder().id(2L).build();
        Transaction tx = Transaction.builder().id(1L).user(owner).build();

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(tx));

        assertThrows(ForbiddenException.class, () -> transactionService.deleteTransaction(1L, 1L));
    }

    @Test
    void updateTransaction_NotFound_ThrowsNotFound() {
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());
        TransactionUpdateRequest req = new TransactionUpdateRequest(new BigDecimal("100"), "Food", "desc");

        assertThrows(ResourceNotFoundException.class, () -> transactionService.updateTransaction(1L, 99L, req));
    }
}