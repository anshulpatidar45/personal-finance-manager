package com.finance.personalfinancemanager.transaction;

import com.finance.personalfinancemanager.category.Category;
import com.finance.personalfinancemanager.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A single financial transaction owned by one user and tagged with one category.
 * The direction (income/expense) is derived from the category type;
 * the amount itself is always a positive decimal.
 */
@Entity
@Table(
        name = "transactions",
        indexes = @Index(name = "idx_transaction_user_date", columnList = "user_id, date")
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Must be a valid, non-deleted category accessible to the owner. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    /** YYYY-MM-DD, never in the future; immutable after creation. */
    @Column(nullable = false)
    private LocalDate date;

    @Column(length = 500)
    private String description;
}