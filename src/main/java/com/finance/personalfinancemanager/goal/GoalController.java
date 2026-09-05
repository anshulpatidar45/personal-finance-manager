package com.finance.personalfinancemanager.goal;

import com.finance.personalfinancemanager.goal.dto.GoalCreateRequest;
import com.finance.personalfinancemanager.goal.dto.GoalResponse;
import com.finance.personalfinancemanager.goal.dto.GoalUpdateRequest;
import com.finance.personalfinancemanager.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GoalResponse create(@Valid @RequestBody GoalCreateRequest request) {
        return goalService.createGoal(SecurityUtils.getCurrentUserId(), request);
    }

    @GetMapping
    public Map<String, List<GoalResponse>> getAll() {
        return Map.of("goals", goalService.getAllGoals(SecurityUtils.getCurrentUserId()));
    }

    @GetMapping("/{id}")
    public GoalResponse get(@PathVariable Long id) {
        return goalService.getGoal(SecurityUtils.getCurrentUserId(), id);
    }

    @PutMapping("/{id}")
    public GoalResponse update(
            @PathVariable Long id,
            @Valid @RequestBody GoalUpdateRequest request
    ) {
        return goalService.updateGoal(SecurityUtils.getCurrentUserId(), id, request);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id) {
        goalService.deleteGoal(SecurityUtils.getCurrentUserId(), id);
        return Map.of("message", "Goal deleted successfully");
    }
}