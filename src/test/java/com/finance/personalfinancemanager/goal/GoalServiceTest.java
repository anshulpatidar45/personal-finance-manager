package com.finance.personalfinancemanager.goal;

import com.finance.personalfinancemanager.exception.ForbiddenException;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock private GoalRepository goalRepository;
    @Mock private TransactionRepository transactionRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private GoalService goalService;

    @Test
    void getGoal_NotOwner_ThrowsForbidden() {
        User owner = User.builder().id(2L).build();
        Goal goal = Goal.builder().id(1L).user(owner).build();

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

        assertThrows(ForbiddenException.class, () -> goalService.getGoal(1L, 1L));
    }

    @Test
    void deleteGoal_NotOwner_ThrowsForbidden() {
        User owner = User.builder().id(2L).build();
        Goal goal = Goal.builder().id(1L).user(owner).build();

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

        assertThrows(ForbiddenException.class, () -> goalService.deleteGoal(1L, 1L));
    }
}