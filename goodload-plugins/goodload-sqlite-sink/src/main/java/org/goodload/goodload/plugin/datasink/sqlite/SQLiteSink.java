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
package org.goodload.goodload.plugin.datasink.sqlite;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goodload.goodload.plugin.datasink.sqlite.data.IterationReportRegistry;
import org.goodload.goodload.plugin.datasink.sqlite.data.SimulationRepository;
import org.goodload.goodload.plugin.datasink.sqlite.models.ActionReportEntity;
import org.goodload.goodload.plugin.datasink.sqlite.models.SimulationEntity;
import org.goodload.goodload.plugin.datasink.sqlite.models.StepSkeletonEntity;
import org.goodload.goodload.reporting.data.ActionReport;
import org.goodload.goodload.reporting.data.SimulationTree;
import org.goodload.goodload.reporting.data.StepSkeletonData;
import org.goodload.goodload.reporting.datasink.Sink;
import org.goodload.goodload.reporting.datasink.SinkSubscriber;

import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.Flow;

/**
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
@Slf4j
@AllArgsConstructor
public class SQLiteSink extends Sink {

    private final IterationReportRegistry iterationReportRegistry;

    private final SimulationRepository simulationRepository;

    private final SQLiteSinkConfigurationProperties sqLiteSinkConfigurationProperties;

    @Override
    public void registerSimulationSkeletonData(SimulationTree simulationTree) {
        var simulationEntity = new SimulationEntity();
        simulationEntity.setSimulationId(simulationTree.getSimulationId());
        simulationEntity.setSimulationName(simulationTree.getSimulationName());
        var stepEntities = simulationTree.getSteps().stream().map(step -> mapStepDataToEntity(step, simulationTree.getSimulationId(), null)).toList();
        simulationEntity.setSteps(stepEntities);
        simulationRepository.saveAndFlush(simulationEntity);
    }

    private StepSkeletonEntity mapStepDataToEntity(StepSkeletonData stepSkeletonData, String simulationId, String parentStepId) {
        var entity = new StepSkeletonEntity();
        entity.setStepName(stepSkeletonData.getStepName());
        entity.setStepId(stepSkeletonData.getStepId());
        entity.setSimulationId(simulationId);
        entity.setParentStepId(parentStepId);
        var subStepEntities = new LinkedList<StepSkeletonEntity>();
        for (var subStepData : stepSkeletonData.getSubSteps()) {
            var subStepEntity = mapStepDataToEntity(subStepData, simulationId, entity.getStepId());
            subStepEntities.add(subStepEntity);
        }
        entity.setSubSteps(subStepEntities);
        return entity;
    }

    @Override
    protected SinkSubscriber createSubscriber() {
        return new SQLiteSinkSubscriber(iterationReportRegistry, sqLiteSinkConfigurationProperties.getBatchSize());
    }

    @Slf4j
    protected static class SQLiteSinkSubscriber implements SinkSubscriber {

        private final LinkedList<ActionReport> batch = new LinkedList<>();

        private final IterationReportRegistry iterationReportRegistry;

        private final int batchSize;

        private final String subscriberId = UUID.randomUUID().toString();

        private Flow.Subscription subscription = null;

        public SQLiteSinkSubscriber(IterationReportRegistry iterationReportRegistry, int batchSize) {
            this.iterationReportRegistry = iterationReportRegistry;
            this.batchSize = batchSize;
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            log.debug("Sink subscriber with ID {} registered", subscriberId);
            this.subscription = subscription;
            subscription.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(ActionReport item) {
            synchronized (batch) {
                batch.add(item);
            }
            if (batch.size() >= batchSize) {
                processBatch();
            }
            subscription.request(Long.MAX_VALUE);
        }

        @Override
        public void onError(Throwable throwable) {
            log.error(String.format("Sink subscriber with ID %s failed with error: ", subscriberId), throwable);
            processBatch();
        }

        @Override
        public void onComplete() {
            log.debug("Sink subscriber with ID {} completed: ", subscriberId);
            processBatch();
        }

        private void processBatch() {
            LinkedList<ActionReport> batchCopy;
            synchronized (batch) {
                // Copy the batch to new list so that we don't block the subscriber while we write data to SQLite
                batchCopy = new LinkedList<>(batch);
                batch.clear();
            }

            try {
                log.debug("Writing {} report rows to sqlite", batchCopy.size());
                iterationReportRegistry.insertAll(batchCopy.stream().map(ActionReportEntity::fromAction).toList());
            } catch (Exception e) {
                log.error("Failed to write iteration report batch", e);
            }
        }

        @Override
        public void close() {
            synchronized (batch) {
                subscription.cancel();
                processBatch();
            }
        }
    }
}
