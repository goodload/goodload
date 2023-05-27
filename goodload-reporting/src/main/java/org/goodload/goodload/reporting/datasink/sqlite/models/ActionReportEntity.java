package org.goodload.goodload.reporting.datasink.sqlite.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.goodload.goodload.reporting.data.ActionReport;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "action_report")
@NoArgsConstructor
@Getter
@Setter
public class ActionReportEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @NotNull
    private String stepId;

    @Column(nullable = false)
    @NotNull
    private Integer iterationIndex;

    @Column(nullable = false)
    @NotNull
    private Long startTimestampInMillis;

    @Column(nullable = false)
    @NotNull
    private Long endTimestampInMillis;

    @Column(nullable = false)
    @NotNull
    private Boolean endedNormally;

    public static ActionReportEntity fromAction(ActionReport actionReport) {
        var entity = new ActionReportEntity();
        entity.setStepId(actionReport.getStepId());
        entity.setEndedNormally(actionReport.isEndedNormally());
        entity.setIterationIndex(actionReport.getIterationIndex());
        entity.setStartTimestampInMillis(actionReport.getStartTimestampInMillis());
        entity.setEndTimestampInMillis(actionReport.getEndTimestampInMillis());
        return entity;
    }
}
