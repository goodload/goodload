package org.goodload.goodload.reporting.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * The simulation structure. Contains all the steps, their IDs and sub-steps.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
@Data
@NoArgsConstructor
public class SimulationTree implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String simulationId;

    private String simulationName;

    private List<StepSkeletonData> steps;
}
