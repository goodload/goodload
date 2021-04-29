package org.goodload.goodload.criteria;

import lombok.AllArgsConstructor;
import org.goodload.goodload.reporting.reports.raw.Report;

import java.util.List;

/**
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
@AllArgsConstructor
public class MinimumFailCountCriteria implements Criteria {

    /**
     * The minimum number of failed reports which will cause the aggregate report to fail.
     */
    private final long minFailCount;

    /**
     * Checks if the given list of report satisfies the minimum fail count criteria.
     * It returns {@code true} when the number of failures is greater than or equal to the
     * minimum count specified.
     * @param rawReports The reports which have to be checked.
     * @return {@code true} if the given criteria is satisfied, else {@code false}
     */
    @Override
    public boolean matches(List<? extends Report> rawReports) {
        long failCount = rawReports
                .stream()
                .filter(report -> !report.isEndedNormally())
                .count();
        return failCount >= minFailCount;
    }
}
