/*
 * Copyright (C) 2023 Divyansh Shekhar Gaur
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
package org.goodload.goodload.reporting.datasink;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.goodload.goodload.reporting.data.ActionReport;
import org.goodload.goodload.reporting.data.SimulationTree;

import java.util.concurrent.SubmissionPublisher;

/**
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
@Slf4j
public abstract class Sink implements AutoCloseable {

    private SinkSubscriber actionReportSubscriber;

    @Getter
    private volatile boolean closed;

    /**
     * Default constructor
     */
    public Sink() {
    }

    protected abstract SinkSubscriber createSubscriber();

    /**
     * Override this method to receive the simulation structure when a simulation is executed.
     * This simulation structure will be helpful in generating the reports.
     *
     * @param simulationTree The simulation structure. Contains all the steps, their IDs and sub-steps.
     */
    public abstract void registerSimulationSkeletonData(SimulationTree simulationTree);

    public void registerPublisher(SubmissionPublisher<ActionReport> actionReportSubmissionPublisher) {
        synchronized (this) {
            if (this.actionReportSubscriber != null) {
                throw new IllegalStateException("Sink already has a registered publisher.");
            }
            if (this.closed) {
                throw new IllegalStateException("Can't register publisher to closed sink");
            }
            actionReportSubscriber = createSubscriber();
            actionReportSubmissionPublisher.subscribe(actionReportSubscriber);
        }
    }

    @Override
    public void close() throws Exception {
        synchronized (this) {
            if (!closed) {
                actionReportSubscriber.close();
                closed = true;
            }
        }
    }
}
