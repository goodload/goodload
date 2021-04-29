package org.goodload.goodload.criteria;

import org.goodload.goodload.reporting.reports.raw.Report;

import java.util.List;

/**
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
public interface Criteria {
    boolean matches(List<? extends Report> rawReports);
}
