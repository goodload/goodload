package org.divsgaur.goodload.reporting.reports.raw;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Report generated for a step.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StepReport extends Report {
    /**
     * Report of children steps.
     */
    private List<Report> subSteps = new ArrayList<>();

    public StepReport(String name) {
        super(name);
    }
}
