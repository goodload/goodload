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
package org.goodload.goodload.reporting.reports.aggregate;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

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
     * Timestamp when the first iteration started
     */
    private long iterationsStartTimestamp;

    /**
     * Timestamp when the last iteration ended
     */
    private long iterationsEndTimestamp;

    /**
     * The total time (in milliseconds) taken by a step to execute.
     * Only includes the actual time spent in executing an iteration
     * and not the time spent in initialization of the steps/iteration
     * by the engine.
     * <br>
     * Note that the initializations/setup you do as part of the step will
     * be counted towards the aggregate. Only the internal boilerplate
     * is ignored.
     * <br>
     * Hence, the totalTimeInMillis may or may not be equal to
     * {@code iterationsEndTimestamp - iterationsStartTimestamp}
     */
    private long totalTimeInMillis;

    /**
     * Average time in milliseconds. It is equal to totalTimeInMillis / number of iterations.
     */
    private long averageTimeInMillis;

    /**
     * If true, then the execution of the step failed due to some error.
     * If false, then the execution completed successfully.
     */
    private boolean errorsOccured = false;

    /**
     * Store number of hits for every second the step was run.
     * The actual time for nHits at index i = startTimeInMillis / 1000 + i
     */
    private List<Float> hitsPerSecond;

    protected AggregateReport(String stepName) {
        this.stepName = stepName;
    }
}
