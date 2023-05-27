package org.goodload.goodload.reporting.datasink;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goodload.goodload.reporting.data.ActionReport;
import org.goodload.goodload.reporting.data.SimulationTree;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

@AllArgsConstructor
@Slf4j
public abstract class Sink {

    protected abstract Flow.Subscriber<ActionReport> createSubscriber();

    /**
     * Override this method to receive the simulation structure when a simulation is executed.
     * This simulation structure will be helpful in generating the reports.
     *
     * @param simulationTree The simulation structure. Contains all the steps, their IDs and sub-steps.
     */
    public abstract void registerSimulationSkeletonData(SimulationTree simulationTree);

    public void registerPublisher(SubmissionPublisher<ActionReport> actionReportSubmissionPublisher) {
        actionReportSubmissionPublisher.subscribe(createSubscriber());
    }
}
