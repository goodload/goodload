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
package org.goodload.goodload.reporting.datasink.sqlite.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;


/**
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
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
