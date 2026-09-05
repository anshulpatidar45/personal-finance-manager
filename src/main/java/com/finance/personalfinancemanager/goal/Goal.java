package com.finance.personalfinancemanager.goal;

import com.finance.personalfinancemanager.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
 * A savings goal. Progress fields (currentProgress, percentage, remaining)
 * are computed on read from the owner's transactions since {@code startDate};
 * they are intentionally NOT persisted.
 */
@Entity
@Table(name = "goals")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 150)
    private String goalName;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal targetAmount;

    /** Must be a future date at creation/update time. */
    @Column(nullable = false)
    private LocalDate targetDate;

    /** Defaults to the creation date when not provided. */
    @Column(nullable = false)
    private LocalDate startDate;
}