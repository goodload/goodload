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
package org.goodload.goodload.reporting.reports.raw;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

/**
 * The performance report generated for a step, or group of steps.
 * @since 1.0
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public abstract class Report implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Name of the step this report belongs to.
     */
    private String stepName;

    /**
     * When the execution started for the step/action/scenario this report belongs to.
     */
    private long startTimestampInMillis;

    /**
     * When the execution started for the step/action/scenario this report belongs to
     */
    private long endTimestampInMillis;

    /**
     * If false, then the execution of the step failed due to some error.
     * If true, then the execution completed successfully.
     */
    private boolean endedNormally = true;

    /**
     * Id of the simulation runner. Can be used for debugging.
     */
    private String runnerId;

    /**
     * Denotes the iteration for which this report was generated.
     */
    private int iterationIndex;

    public long getTotalTimeInMillis() {
        return endTimestampInMillis - startTimestampInMillis;
    }

    protected Report(String stepName) {
        this.stepName = stepName;
    }
}
