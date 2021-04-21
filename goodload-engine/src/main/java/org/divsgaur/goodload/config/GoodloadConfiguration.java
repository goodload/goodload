package org.divsgaur.goodload.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.divsgaur.goodload.reporting.ReportAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoodloadConfiguration {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return new ObjectMapper();
    }

    @Bean
    public ReportAggregator reportAggregator() {
        return new ReportAggregator();
    }
}
