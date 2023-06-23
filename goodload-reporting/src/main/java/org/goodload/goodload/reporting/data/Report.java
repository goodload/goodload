/*
 * Copyright (C) 2023 Divyansh Shekhar Gaur
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
package org.goodload.goodload.reporting.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * The performance report generated for a step, or group of steps.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public abstract class Report implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID of the step this report belongs to.
     */
    private String stepId;
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
     * Id of the simulation runner.
     */
    private String runnerId;

    public long getTotalTimeInMillis() {
        return endTimestampInMillis - startTimestampInMillis;
    }

    protected Report(String stepId) {
        this.stepId = stepId;
    }

    protected Report(String stepId, String stepName) {
        this.stepId = stepId;
        this.stepName = stepName;
    }
}
