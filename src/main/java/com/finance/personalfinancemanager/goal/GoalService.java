package com.finance.personalfinancemanager.goal;

import com.finance.personalfinancemanager.category.CategoryType;
import com.finance.personalfinancemanager.exception.BadRequestException;
import com.finance.personalfinancemanager.exception.ForbiddenException;
import com.finance.personalfinancemanager.exception.ResourceNotFoundException;
import com.finance.personalfinancemanager.goal.dto.GoalCreateRequest;
import com.finance.personalfinancemanager.goal.dto.GoalResponse;
import com.finance.personalfinancemanager.goal.dto.GoalUpdateRequest;
import com.finance.personalfinancemanager.transaction.TransactionRepository;
import com.finance.personalfinancemanager.user.User;
import com.finance.personalfinancemanager.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Transactional
    public GoalResponse createGoal(Long userId, GoalCreateRequest request) {
        // Defaults to creation date if not provided
        LocalDate startDate = request.startDate() != null ? request.startDate() : LocalDate.now();

        if (startDate.isAfter(LocalDate.now())) {
            throw new BadRequestException("Start date cannot be a future date");
        }
        if (!request.targetDate().isAfter(startDate)) {
            throw new BadRequestException("Target date must be after start date");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Goal goal = Goal.builder()
                .user(user)
                .goalName(request.goalName())
                .targetAmount(request.targetAmount())
                .targetDate(request.targetDate())
                .startDate(startDate)
                .build();

        return toResponse(goalRepository.save(goal), userId);
    }

    @Transactional(readOnly = true)
    public List<GoalResponse> getAllGoals(Long userId) {
        return goalRepository.findAllByUserIdOrderByTargetDateAsc(userId).stream()
                .map(goal -> toResponse(goal, userId))
                .toList();
    }

    @Transactional(readOnly = true)
    public GoalResponse getGoal(Long userId, Long goalId) {
        Goal goal = getOwnedGoal(goalId, userId);
        return toResponse(goal, userId);
    }

    @Transactional
    public GoalResponse updateGoal(Long userId, Long goalId, GoalUpdateRequest request) {
        Goal goal = getOwnedGoal(goalId, userId);

        boolean hasAmount = request.targetAmount() != null;
        boolean hasDate = request.targetDate() != null;

        if (!hasAmount && !hasDate) {
            throw new BadRequestException("No valid fields provided for update");
        }

        if (hasAmount) {
            goal.setTargetAmount(request.targetAmount());
        }
        if (hasDate) {
            if (!request.targetDate().isAfter(goal.getStartDate())) {
                throw new BadRequestException("Target date must be after start date");
            }
            goal.setTargetDate(request.targetDate());
        }

        return toResponse(goalRepository.save(goal), userId);
    }

    @Transactional
    public void deleteGoal(Long userId, Long goalId) {
        Goal goal = getOwnedGoal(goalId, userId);
        goalRepository.delete(goal);
    }

    /** Fetches a goal and enforces multi-tenant data isolation (403 if not owner). */
    private Goal getOwnedGoal(Long goalId, Long userId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        if (!goal.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Access denied");
        }
        return goal;
    }

    /** Calculates progress dynamically based on transactions since the goal's start date. */
    private GoalResponse toResponse(Goal goal, Long userId) {
        BigDecimal income = Optional.ofNullable(transactionRepository.sumByTypeSince(
                userId, CategoryType.INCOME, goal.getStartDate()
        )).orElse(BigDecimal.ZERO);

        BigDecimal expense = Optional.ofNullable(transactionRepository.sumByTypeSince(
                userId, CategoryType.EXPENSE, goal.getStartDate()
        )).orElse(BigDecimal.ZERO);

        BigDecimal progress = income.subtract(expense);
        BigDecimal remaining = goal.getTargetAmount().subtract(progress);

        // Remaining amount cannot be negative
        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            remaining = BigDecimal.ZERO;
        }

        double percentage = 0.0;
        if (goal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0 && progress.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal rawPercentage = progress
                    .multiply(BigDecimal.valueOf(100))
                    .divide(goal.getTargetAmount(), 2, RoundingMode.HALF_UP);
            percentage = Math.min(100.0, rawPercentage.doubleValue());
        }

        return new GoalResponse(
                goal.getId(),
                goal.getGoalName(),
                goal.getTargetAmount().setScale(2, RoundingMode.HALF_UP),
                goal.getTargetDate(),
                goal.getStartDate(),
                progress.setScale(2, RoundingMode.HALF_UP),
                percentage,
                remaining.setScale(2, RoundingMode.HALF_UP)
        );
    }
}