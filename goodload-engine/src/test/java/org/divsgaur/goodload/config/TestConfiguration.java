package org.divsgaur.goodload.config;

import org.divsgaur.goodload.reporting.ReportAggregator;
import org.springframework.context.annotation.Bean;

/**
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 */
@org.springframework.boot.test.context.TestConfiguration
public class TestConfiguration {
    @Bean
    public ReportAggregator reportAggregator() {
        return new ReportAggregator();
    }
}
