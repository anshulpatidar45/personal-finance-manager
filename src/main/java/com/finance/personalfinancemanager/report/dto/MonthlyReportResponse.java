package com.finance.personalfinancemanager.report.dto;

import java.math.BigDecimal;
import java.util.Map;

/** Monthly financial report grouped by category. */
public record MonthlyReportResponse(
        int month,
        int year,
        Map<String, BigDecimal> totalIncome,
        Map<String, BigDecimal> totalExpenses,
        BigDecimal netSavings
) {}