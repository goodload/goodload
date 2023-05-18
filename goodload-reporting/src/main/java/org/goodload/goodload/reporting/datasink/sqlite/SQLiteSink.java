package org.goodload.goodload.reporting.datasink.sqlite;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goodload.goodload.reporting.data.ActionReport;
import org.goodload.goodload.reporting.datasink.Sink;

import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.Flow;

@Slf4j
@AllArgsConstructor
public class SQLiteSink extends Sink {

    private final IterationReportRepository iterationReportRepository;

    private final SQLiteSinkConfigurationProperties sqLiteSinkConfigurationProperties;

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
            subscription.request(Integer.MAX_VALUE);
        }

        @Override
        public void onNext(ActionReport item) {
            batch.add(item);
            if (batch.size() >= batchSize) {
                processBatch();
            }
            subscription.request(Integer.MAX_VALUE);
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
            iterationReportRepository.saveAll(batch);
            batch.clear();
        }
    }
}
