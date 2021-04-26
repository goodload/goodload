/*
Copyright (C) 2021 Goodload

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.divsgaur.goodload.reporting.reports.aggregate;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Report generated after aggregating individual reports generated when simulations are run.
 * 
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
@Data
@NoArgsConstructor
public abstract class AggregateReport implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Name of the step this report belongs to.
     */
    private String stepName;

    /**
     * The total time (in milliseconds) taken by a step to execute.
     */
    private long totalTimeInMillis;

    /**
     * Average time in milliseconds.
     */
    private long averageTimeInMillis;

    /**
     * If true, then the execution of the step failed due to some error.
     * If false, then the execution completed successfully.
     */
    private boolean errorsOccured = false;

    protected AggregateReport(String stepName) {
        this.stepName = stepName;
    }
}
