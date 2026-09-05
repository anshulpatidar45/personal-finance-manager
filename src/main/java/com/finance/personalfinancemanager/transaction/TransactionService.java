package com.finance.personalfinancemanager.transaction;

import com.finance.personalfinancemanager.category.Category;
import com.finance.personalfinancemanager.category.CategoryRepository;
import com.finance.personalfinancemanager.category.CategoryType;
import com.finance.personalfinancemanager.exception.BadRequestException;
import com.finance.personalfinancemanager.exception.ForbiddenException;
import com.finance.personalfinancemanager.exception.ResourceNotFoundException;
import com.finance.personalfinancemanager.transaction.dto.TransactionCreateRequest;
import com.finance.personalfinancemanager.transaction.dto.TransactionResponse;
import com.finance.personalfinancemanager.transaction.dto.TransactionUpdateRequest;
import com.finance.personalfinancemanager.user.User;
import com.finance.personalfinancemanager.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public TransactionResponse createTransaction(Long userId, TransactionCreateRequest request) {
        // Extra safety check (Bean validation already handles @PastOrPresent)
        if (request.date().isAfter(LocalDate.now())) {
            throw new BadRequestException("Date cannot be a future date");
        }

        Category category = categoryRepository
                .findAccessibleByName(request.category().trim(), userId)
                .orElseThrow(() -> new BadRequestException("Invalid category"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Transaction transaction = Transaction.builder()
                .user(user)
                .category(category)
                .amount(request.amount())
                .date(request.date())
                .description(request.description())
                .build();

        return toResponse(transactionRepository.save(transaction));
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactions(
            Long userId, LocalDate startDate, LocalDate endDate, Long categoryId, String type
    ) {
        CategoryType categoryType = parseType(type);

        Specification<Transaction> spec = TransactionSpecifications.filter(
                userId, startDate, endDate, categoryId, categoryType
        );

        return transactionRepository.findAll(spec).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public TransactionResponse updateTransaction(Long userId, Long id, TransactionUpdateRequest request) {
        Transaction transaction = getOwnedTransaction(id, userId);

        boolean hasAmount = request.amount() != null;
        boolean hasCategory = StringUtils.hasText(request.category());
        boolean hasDescription = request.description() != null;

        if (!hasAmount && !hasCategory && !hasDescription) {
            throw new BadRequestException("No valid fields provided for update");
        }

        if (hasAmount) transaction.setAmount(request.amount());

        if (hasCategory) {
            Category category = categoryRepository
                    .findAccessibleByName(request.category().trim(), userId)
                    .orElseThrow(() -> new BadRequestException("Invalid category"));
            transaction.setCategory(category);
        }

        if (hasDescription) transaction.setDescription(request.description());

        return toResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public void deleteTransaction(Long userId, Long id) {
        Transaction transaction = getOwnedTransaction(id, userId);
        // Hard delete ensures it disappears from Goals and Reports automatically
        transactionRepository.delete(transaction);
    }

    /** Fetches a transaction and enforces multi-tenant data isolation (403 if not owner). */
    private Transaction getOwnedTransaction(Long id, Long userId) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (!transaction.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Access denied");
        }
        return transaction;
    }

    private CategoryType parseType(String type) {
        if (!StringUtils.hasText(type)) return null;
        try {
            return CategoryType.valueOf(type.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid transaction type");
        }
    }

    private TransactionResponse toResponse(Transaction t) {
        return new TransactionResponse(
                t.getId(),
                t.getAmount(),
                t.getDate(),
                t.getCategory().getName(),
                t.getDescription(),
                t.getCategory().getType()
        );
    }
}