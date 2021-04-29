/*
 * Copyright (C) 2021 Divyansh Shekhar Gaur
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
