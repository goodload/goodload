package org.goodload.goodload.reporting.datasink.sqlite.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "simulation_metadata")
@Getter
@Setter
@NoArgsConstructor
public class SimulationEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String simulationId;

    @Column(nullable = false)
    @NotNull
    private String simulationName;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "simulationId")
    @Where(clause = "parent_step_id IS NULL")
    private List<StepSkeletonEntity> steps;
}
