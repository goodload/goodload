package org.divsgaur.goodload.reporting.reports.raw;

import lombok.*;
import org.springframework.lang.NonNull;

import java.io.Serializable;

/**
 * The performance report generated for a step, or group of steps.
 * @since 1.0
 */
@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public abstract class Report implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Name of the step this report belongs to.
     */
    @NonNull
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
}
