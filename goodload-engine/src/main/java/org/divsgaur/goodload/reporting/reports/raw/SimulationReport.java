package org.divsgaur.goodload.reporting.reports.raw;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Report of entire simulation.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SimulationReport extends Report {

    /**
     * Report of scenarios in the simulation
     */
    private List<ActionReport> scenarios = new ArrayList<>();

    public SimulationReport(String name) {
        super(name);
    }
}
