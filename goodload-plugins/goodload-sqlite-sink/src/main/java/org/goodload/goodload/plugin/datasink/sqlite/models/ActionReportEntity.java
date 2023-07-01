/*
 * Copyright (C) 2023 Divyansh Shekhar Gaur
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.goodload.goodload.plugin.datasink.sqlite.models;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.goodload.goodload.reporting.data.ActionReport;
import org.springframework.data.domain.Persistable;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
@Entity
@Table(name = "action_report")
@NoArgsConstructor
@Getter
@Setter
public class ActionReportEntity implements Persistable<String>, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column
    @Id
    private String id;

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
        entity.setId(UUID.randomUUID().toString());
        entity.setStepId(actionReport.getStepId());
        entity.setEndedNormally(actionReport.isEndedNormally());
        entity.setIterationIndex(actionReport.getIterationIndex());
        entity.setStartTimestampInMillis(actionReport.getStartTimestampInMillis());
        entity.setEndTimestampInMillis(actionReport.getEndTimestampInMillis());
        return entity;
    }

    /**
     * Always returns {@code true} to force insertion and disable updating.
     * It overrides default behavior of hibernate {@link org.springframework.data.repository.CrudRepository#save}
     * method to do insert without select.
     *
     * @return {@code true}
     */
    @Override
    public boolean isNew() {
        return true;
    }
}
