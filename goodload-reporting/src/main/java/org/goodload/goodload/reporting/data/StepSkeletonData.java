package org.goodload.goodload.reporting.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class StepSkeletonData {
    @Id
    private String stepId;
    private String stepName;
    private String parentStepId;
}