package com.finance.personalfinancemanager.report.dto;

import java.math.BigDecimal;
import java.util.Map;

/** Yearly financial report grouped by category. */
public record YearlyReportResponse(
        int year,
        Map<String, BigDecimal> totalIncome,
        Map<String, BigDecimal> totalExpenses,
        BigDecimal netSavings
) {}