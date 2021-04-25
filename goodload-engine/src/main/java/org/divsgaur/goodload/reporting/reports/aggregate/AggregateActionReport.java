package org.divsgaur.goodload.reporting.reports.aggregate;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.divsgaur.goodload.reporting.reports.raw.ActionReport;

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

    public AggregateActionReport(String name) {
        super(name);
    }
}
