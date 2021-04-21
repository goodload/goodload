package org.divsgaur.goodload.reporting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * The performance report generated for a step.
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Report implements Serializable {
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
     * Report of children steps.
     */
    private List<Report> subSteps;
    /**
     * If false, then the execution of the step failed due to some error.
     * If true, then the execution completed successfully.
     */
    private boolean endedNormally = true;

    /**
     * Id of the simulation runner. Can be used for debugging.
     */
    private String runnerId;
}
