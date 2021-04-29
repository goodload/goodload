/*
 * Copyright (C) 2021 Goodload
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
package org.goodload.goodload.reporting.reports.aggregate;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.goodload.goodload.reporting.reports.raw.ActionReport;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AggregateActionReport extends AggregateReport {
    /**
     * Number of times the scenario was executed if this report belongs to a scenario.
     */
    private int iterations;

    /**
     * Report of children steps.
     */
    private List<AggregateActionReport> subSteps = new ArrayList<>();

    /**
     * Raw report used for calculating aggregate.
     */
    private List<ActionReport> rawReports = new ArrayList<>();

    /**
     * If the action passed all the fail-pass criteria then true, else false.
     *
     */
    private boolean passed = false;

    public AggregateActionReport(String name) {
        super(name);
    }
}
