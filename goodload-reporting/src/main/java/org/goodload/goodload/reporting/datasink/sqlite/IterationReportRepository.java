package org.goodload.goodload.reporting.datasink.sqlite;

import org.goodload.goodload.reporting.data.ActionReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IterationReportRepository extends JpaRepository<ActionReport, Integer> {

}
