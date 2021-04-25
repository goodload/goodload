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

    public AggregateReport(String stepName) {
        this.stepName = stepName;
    }
}
