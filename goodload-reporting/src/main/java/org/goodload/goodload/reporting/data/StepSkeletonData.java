package org.goodload.goodload.reporting.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * Metadata of steps of a simulation.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
@Data
@NoArgsConstructor
public class StepSkeletonData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String stepId;

    private String stepName;

    private List<StepSkeletonData> subSteps;
}