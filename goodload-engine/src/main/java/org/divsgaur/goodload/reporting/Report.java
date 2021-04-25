package org.divsgaur.goodload.reporting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The performance report generated for a step, or group of steps.
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
     * When the execution started for the step/action/scenario this report belongs to.
     */
    private long startTimestampInMillis;

    /**
     * When the execution started for the step/action/scenario this report belongs to
     */
    private long endTimestampInMillis;

    /**
     * Report of children steps.
     */
    private List<Report> subSteps = new ArrayList<>();

    /**
     * Report of iterations that happened for the simulation.
     * It is null for substeps of a simulation.
     */
    private List<Report> iterations = new ArrayList<>();

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
}
