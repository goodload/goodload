package org.goodload.goodload.reporting.data;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class SimulationTree implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String simulationId;
    private String simulationName;

    @OneToMany(fetch = FetchType.LAZY)
    private List<StepSkeletonData> steps;
}
