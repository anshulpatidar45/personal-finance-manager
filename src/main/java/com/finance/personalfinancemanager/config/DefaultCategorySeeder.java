package com.finance.personalfinancemanager.config;

import com.finance.personalfinancemanager.category.Category;
import com.finance.personalfinancemanager.category.CategoryRepository;
import com.finance.personalfinancemanager.category.CategoryType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Seeds the 7 predefined system categories on first boot.
 * Idempotent: skips when categories already exist.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultCategorySeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            return;
        }

        List<Category> defaults = List.of(
                category("Salary", CategoryType.INCOME),
                category("Food", CategoryType.EXPENSE),
                category("Rent", CategoryType.EXPENSE),
                category("Transportation", CategoryType.EXPENSE),
                category("Entertainment", CategoryType.EXPENSE),
                category("Healthcare", CategoryType.EXPENSE),
                category("Utilities", CategoryType.EXPENSE)
        );

        categoryRepository.saveAll(defaults);
        log.info("Seeded {} default categories", defaults.size());
    }

    private Category category(String name, CategoryType type) {
        return Category.builder()
                .name(name)
                .type(type)
                .defaultCategory(true)
                .user(null)
                .build();
    }
}