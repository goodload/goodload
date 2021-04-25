package org.divsgaur.goodload.reporting.reports.raw;

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
public class ScenarioReport extends Report {
    /**
     * Report of iterations that happened for the simulation.
     * It is null for substeps of a simulation.
     */
    private List<Report> iterations = new ArrayList<>();

    public ScenarioReport(String name) {
        super(name);
    }
}
