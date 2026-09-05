package com.finance.personalfinancemanager.report;

import com.finance.personalfinancemanager.category.CategoryType;
import com.finance.personalfinancemanager.exception.BadRequestException;
import com.finance.personalfinancemanager.report.dto.MonthlyReportResponse;
import com.finance.personalfinancemanager.report.dto.YearlyReportResponse;
import com.finance.personalfinancemanager.transaction.Transaction;
import com.finance.personalfinancemanager.transaction.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public MonthlyReportResponse getMonthlyReport(Long userId, int year, int month) {
        if (month < 1 || month > 12) {
            throw new BadRequestException("Month must be between 1 and 12");
        }

        YearMonth yearMonth;
        try {
            yearMonth = YearMonth.of(year, month);
        } catch (Exception e) {
            throw new BadRequestException("Invalid year or month");
        }

        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        return buildReport(userId, start, end, year, month);
    }

    @Transactional(readOnly = true)
    public YearlyReportResponse getYearlyReport(Long userId, int year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        List<Transaction> transactions = transactionRepository.findAllInPeriod(userId, start, end);

        Map<String, BigDecimal> income = new LinkedHashMap<>();
        Map<String, BigDecimal> expense = new LinkedHashMap<>();
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (Transaction t : transactions) {
            String categoryName = t.getCategory().getName();
            BigDecimal amount = t.getAmount();

            if (t.getCategory().getType() == CategoryType.INCOME) {
                income.merge(categoryName, amount, BigDecimal::add);
                totalIncome = totalIncome.add(amount);
            } else {
                expense.merge(categoryName, amount, BigDecimal::add);
                totalExpense = totalExpense.add(amount);
            }
        }

        BigDecimal netSavings = totalIncome.subtract(totalExpense);

        return new YearlyReportResponse(
                year,
                normalize(income),
                normalize(expense),
                netSavings.setScale(2, RoundingMode.HALF_UP)
        );
    }

    private MonthlyReportResponse buildReport(Long userId, LocalDate start, LocalDate end, int year, int month) {
        List<Transaction> transactions = transactionRepository.findAllInPeriod(userId, start, end);

        Map<String, BigDecimal> income = new LinkedHashMap<>();
        Map<String, BigDecimal> expense = new LinkedHashMap<>();
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (Transaction t : transactions) {
            String categoryName = t.getCategory().getName();
            BigDecimal amount = t.getAmount();

            if (t.getCategory().getType() == CategoryType.INCOME) {
                income.merge(categoryName, amount, BigDecimal::add);
                totalIncome = totalIncome.add(amount);
            } else {
                expense.merge(categoryName, amount, BigDecimal::add);
                totalExpense = totalExpense.add(amount);
            }
        }

        BigDecimal netSavings = totalIncome.subtract(totalExpense);

        return new MonthlyReportResponse(
                month,
                year,
                normalize(income),
                normalize(expense),
                netSavings.setScale(2, RoundingMode.HALF_UP)
        );
    }

    /** Ensures all BigDecimal values in the map have exactly 2 decimal places. */
    private Map<String, BigDecimal> normalize(Map<String, BigDecimal> values) {
        Map<String, BigDecimal> normalized = new LinkedHashMap<>();
        values.forEach((key, value) -> normalized.put(key, value.setScale(2, RoundingMode.HALF_UP)));
        return normalized;
    }
}