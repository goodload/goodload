package org.goodload.goodload.reporting.data;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SimulationTree implements Serializable {
    private static final long serialVersionUID = 1L;

    private String simulationId;
    private String simulationName;
    private List<StepSkeletonData> steps;

    public static class StepSkeletonData {
        private String stepName;
        private String stepId;
        private String parentStepId;
    }
}
