package com.finance.personalfinancemanager.category;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Persistence access for {@link Category} with tenant-aware lookups.
 * "Accessible" always means: global defaults OR owned by the given user.
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByNameIgnoreCaseAndUserIsNull(String name);

    Optional<Category> findByNameIgnoreCaseAndUserId(String name, Long userId);

    boolean existsByNameIgnoreCaseAndUserIsNull(String name);

    boolean existsByNameIgnoreCaseAndUserId(String name, Long userId);

    boolean existsByNameIgnoreCase(String name);

    /** All categories visible to a user: defaults first, then own customs (by id). */
    @Query("""
        SELECT c FROM Category c
        WHERE c.user IS NULL OR c.user.id = :userId
        ORDER BY c.id
    """)
    List<Category> findAllAccessibleByUserId(@Param("userId") Long userId);

    /** Case-insensitive lookup restricted to categories the user may actually use. */
    @Query("""
        SELECT c FROM Category c
        WHERE LOWER(c.name) = LOWER(:name)
        AND (c.user IS NULL OR c.user.id = :userId)
    """)
    Optional<Category> findAccessibleByName(@Param("name") String name, @Param("userId") Long userId);
}