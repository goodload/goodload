package org.goodload.goodload.reporting.datasink.sqlite.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "action_metadata")
@NoArgsConstructor
@Getter
@Setter
public class StepSkeletonEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String stepId;

    @Column(nullable = false)
    @NotNull
    private String stepName;

    @Column(nullable = false)
    @NotNull
    private String simulationId;

    @Column
    private String parentStepId;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "parentStepId")
    private List<StepSkeletonEntity> subSteps;
}
