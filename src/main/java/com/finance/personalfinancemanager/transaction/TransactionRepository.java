package com.finance.personalfinancemanager.transaction;

import com.finance.personalfinancemanager.category.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Persistence access for {@link Transaction}.
 * Every query is tenant-scoped (filtered by user id) to guarantee data isolation.
 */
public interface TransactionRepository
        extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    /** Guard used before allowing a category deletion. */
    boolean existsByCategoryIdAndUserId(Long categoryId, Long userId);

    /**
     * Sums amounts of one type for a user on/after a start date (savings-goal progress).
     * Returns {@code null} when no rows match — callers must null-guard.
     */
    @Query("""
        SELECT SUM(t.amount) FROM Transaction t
        WHERE t.user.id = :userId
        AND t.category.type = :type
        AND t.date >= :startDate
    """)
    BigDecimal sumByTypeSince(
            @Param("userId") Long userId,
            @Param("type") CategoryType type,
            @Param("startDate") LocalDate startDate
    );

    /** All user transactions inside an inclusive date range (monthly/yearly reports). */
    @Query("""
        SELECT t FROM Transaction t
        WHERE t.user.id = :userId
        AND t.date BETWEEN :start AND :end
    """)
    List<Transaction> findAllInPeriod(
            @Param("userId") Long userId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
}