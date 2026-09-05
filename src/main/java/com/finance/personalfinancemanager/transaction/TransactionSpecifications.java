package com.finance.personalfinancemanager.transaction;

import com.finance.personalfinancemanager.category.CategoryType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Builds dynamic JPA Criteria queries for filtering transactions. */
public final class TransactionSpecifications {

    private TransactionSpecifications() {}

    public static Specification<Transaction> filter(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            Long categoryId,
            CategoryType type
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Strict tenant isolation
            predicates.add(cb.equal(root.get("user").get("id"), userId));

            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), endDate));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get("category").get("type"), type));
            }

            // Sort by newest date first, then newest ID first
            query.orderBy(cb.desc(root.get("date")), cb.desc(root.get("id")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}