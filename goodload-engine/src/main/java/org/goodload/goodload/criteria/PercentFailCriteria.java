package org.goodload.goodload.criteria;

import lombok.AllArgsConstructor;
import org.goodload.goodload.reporting.reports.raw.Report;

import java.util.List;

/**
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
@AllArgsConstructor
public class PercentFailCriteria implements Criteria {

    /**
     * The minimum percentage of failed reports which will cause the aggregate report to fail.
     */
    private final long minPercentOfFailures;

    /**
     * Checks if the given list of report satisfies the minimum percent fail count criteria.
     * It returns {@code true} when the percentage of failures is greater than or equal to the
     * minimum percentage specified.
     * @param rawReports The reports which have to be checked.
     * @return {@code true} if the given criteria is satisfied, else {@code false}
     */
    @Override
    public boolean matches(List<? extends Report> rawReports) {
        long failCount = rawReports
                .stream()
                .filter(report -> !report.isEndedNormally())
                .count();
        return failCount / rawReports.size() >= minPercentOfFailures;
    }
}
