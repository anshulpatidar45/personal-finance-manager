package com.finance.personalfinancemanager.report;

import com.finance.personalfinancemanager.report.dto.MonthlyReportResponse;
import com.finance.personalfinancemanager.report.dto.YearlyReportResponse;
import com.finance.personalfinancemanager.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /** GET /api/reports/monthly/{year}/{month} -> 200 */
    @GetMapping("/monthly/{year}/{month}")
    public MonthlyReportResponse monthlyReport(
            @PathVariable int year,
            @PathVariable int month
    ) {
        return reportService.getMonthlyReport(SecurityUtils.getCurrentUserId(), year, month);
    }

    /** GET /api/reports/yearly/{year} -> 200 */
    @GetMapping("/yearly/{year}")
    public YearlyReportResponse yearlyReport(@PathVariable int year) {
        return reportService.getYearlyReport(SecurityUtils.getCurrentUserId(), year);
    }
}