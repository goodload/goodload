package org.divsgaur.goodload.reporting;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Report generated after aggregating individual reports generated when simulations are run.
 * 
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 */
@Data
@NoArgsConstructor
public class AggregateReport implements Serializable {
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
     * Number of times the simulation was executed.
     */
    private int iterations;

    /**
     * Report of children steps.
     */
    private List<AggregateReport> subSteps;

    /**
     * Raw report used for calculating aggregate.
     */
    private List<Report> rawReports;

    /**
     * If true, then the execution of the step failed due to some error.
     * If false, then the execution completed successfully.
     */
    private boolean errorsOccured = false;

    public AggregateReport(String stepName) {
        this.stepName = stepName;
    }
}
