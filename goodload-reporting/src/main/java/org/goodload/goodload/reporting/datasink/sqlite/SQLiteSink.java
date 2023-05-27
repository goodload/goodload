package org.goodload.goodload.reporting.datasink.sqlite;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goodload.goodload.reporting.data.ActionReport;
import org.goodload.goodload.reporting.data.SimulationTree;
import org.goodload.goodload.reporting.data.StepSkeletonData;
import org.goodload.goodload.reporting.datasink.Sink;
import org.goodload.goodload.reporting.datasink.sqlite.models.StepSkeletonEntity;
import org.goodload.goodload.reporting.datasink.sqlite.models.ActionReportEntity;
import org.goodload.goodload.reporting.datasink.sqlite.models.SimulationEntity;

import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.Flow;

@Slf4j
@AllArgsConstructor
public class SQLiteSink extends Sink {

    private final IterationReportRepository iterationReportRepository;

    private final SimulationRepository simulationRepository;

    private final SQLiteSinkConfigurationProperties sqLiteSinkConfigurationProperties;

    @Override
    public void registerSimulationSkeletonData(SimulationTree simulationTree) {
        var simulationEntity = new SimulationEntity();
        simulationEntity.setSimulationId(simulationTree.getSimulationId());
        simulationEntity.setSimulationName(simulationTree.getSimulationName());
        var stepEntities = simulationTree
                .getSteps()
                .stream()
                .map(step ->
                        mapStepDataToEntity(
                                step,
                                simulationTree.getSimulationId(),
                                null))
                .toList();
        simulationEntity.setSteps(stepEntities);
        simulationRepository.saveAndFlush(simulationEntity);
    }

    private StepSkeletonEntity mapStepDataToEntity(StepSkeletonData stepSkeletonData, String simulationId,
                                                   String parentStepId) {
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
    protected Flow.Subscriber<ActionReport> createSubscriber() {
        return new ActionReportSubscriber(iterationReportRepository, sqLiteSinkConfigurationProperties.getBatchSize());
    }

    @Slf4j
    protected static class ActionReportSubscriber implements Flow.Subscriber<ActionReport> {

        private final LinkedList<ActionReport> batch = new LinkedList<>();
        private final IterationReportRepository iterationReportRepository;
        private final int batchSize;

        private final String subscriberId = UUID.randomUUID().toString();

        private Flow.Subscription subscription = null;

        public ActionReportSubscriber(IterationReportRepository iterationReportRepository, int batchSize) {
            this.iterationReportRepository = iterationReportRepository;
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
            batch.add(item);
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
            // TODO: Run in transaction to speed up writes
            try {
                iterationReportRepository.saveAll(batch.stream().map(ActionReportEntity::fromAction).toList());
            } catch (Exception e) {
                log.error("Failed to write iteration report batch", e);
            }
            batch.clear();
        }
    }
}
