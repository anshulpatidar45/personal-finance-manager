package com.finance.personalfinancemanager.goal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Persistence access for {@link Goal}. */
public interface GoalRepository extends JpaRepository<Goal, Long> {

    /** Tenant-scoped list, nearest target date first. */
    List<Goal> findAllByUserIdOrderByTargetDateAsc(Long userId);
}