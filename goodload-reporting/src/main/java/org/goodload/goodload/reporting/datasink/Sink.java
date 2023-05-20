package org.goodload.goodload.reporting.datasink;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goodload.goodload.reporting.data.ActionReport;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

@AllArgsConstructor
@Slf4j
public abstract class Sink {

    protected abstract Flow.Subscriber<ActionReport> createSubscriber();

    public void registerPublisher(SubmissionPublisher<ActionReport> actionReportSubmissionPublisher) {
        actionReportSubmissionPublisher.subscribe(createSubscriber());
    }
}
