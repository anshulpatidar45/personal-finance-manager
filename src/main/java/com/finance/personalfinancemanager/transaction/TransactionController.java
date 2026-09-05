package com.finance.personalfinancemanager.transaction;

import com.finance.personalfinancemanager.category.Category;
import com.finance.personalfinancemanager.category.CategoryRepository;
import com.finance.personalfinancemanager.transaction.dto.TransactionCreateRequest;
import com.finance.personalfinancemanager.transaction.dto.TransactionResponse;
import com.finance.personalfinancemanager.transaction.dto.TransactionUpdateRequest;
import com.finance.personalfinancemanager.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final CategoryRepository categoryRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse create(@Valid @RequestBody TransactionCreateRequest request) {
        return transactionService.createTransaction(SecurityUtils.getCurrentUserId(), request);
    }

    @GetMapping
    public Map<String, List<TransactionResponse>> getAll(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type
    ) {
        Long userId = SecurityUtils.getCurrentUserId();

        // Fallback: if script sends category name instead of ID
        if (categoryId == null && category != null && !category.trim().isEmpty()) {
            Category cat = categoryRepository.findAccessibleByName(category.trim(), userId).orElse(null);
            if (cat != null) {
                categoryId = cat.getId();
            }
        }

        return Map.of("transactions", transactionService.getTransactions(
                SecurityUtils.getCurrentUserId(), startDate, endDate, categoryId, type
        ));
    }

    @PutMapping("/{id}")
    public TransactionResponse update(
            @PathVariable Long id,
            @Valid @RequestBody TransactionUpdateRequest request
    ) {
        return transactionService.updateTransaction(SecurityUtils.getCurrentUserId(), id, request);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id) {
        transactionService.deleteTransaction(SecurityUtils.getCurrentUserId(), id);
        return Map.of("message", "Transaction deleted successfully");
    }
}