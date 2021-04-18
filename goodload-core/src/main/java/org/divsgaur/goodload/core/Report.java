package org.divsgaur.goodload.core;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The performance report generated for a step.
 */
@Data
public class Report implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Name of the step this report belongs to.
     */
    private String stepName;

    /**
     * The total time (in milliseconds) taken by a step to execute.
     */
    private long timeInMillis;

    /**
     * Report of the child steps.
     */
    private List<Report> subSteps = new ArrayList<>();

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
