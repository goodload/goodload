/*
 * Copyright (C) 2021 Divyansh Shekhar Gaur
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
package org.goodload.goodload.reporting.reports.raw;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Report of a scenario.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ActionReport extends Report {
    /**
     * Report of iterations that happened for the simulation.
     * It is null for substeps of a simulation.
     */
    private List<ActionReport> iterations = new ArrayList<>();

    /**
     * Report of children steps.
     */
    private List<ActionReport> subSteps = new ArrayList<>();

    public ActionReport(String name) {
        super(name);
    }
}