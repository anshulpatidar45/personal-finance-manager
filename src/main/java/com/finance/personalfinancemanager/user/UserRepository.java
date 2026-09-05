package com.finance.personalfinancemanager.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/** Persistence access for {@link User}. */
public interface UserRepository extends JpaRepository<User, Long> {

    /** Finds a user by exact (already normalized) username/email. */
    Optional<User> findByUsername(String username);

    /** Used at registration to enforce uniqueness (409 on duplicates). */
    boolean existsByUsername(String username);
}
