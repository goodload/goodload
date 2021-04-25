package org.divsgaur.goodload.reporting.reports.aggregate;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AggregateSimulationReport extends AggregateReport {
    /**
     * Report of scenarios.
     */
    private List<AggregateActionReport> scenarios = new ArrayList<>();

    public AggregateSimulationReport(String name) {
        super(name);
    }
}
