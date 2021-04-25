package org.divsgaur.goodload.dsl;

import lombok.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Each scenario iteration is executed in a separate session.
 * A session holds information about the current iteration.
 * It can be used to keep track of results of previous steps that have been executed.
 * It is not thread-safe.
 */
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Session {
    private final Map<String, Object> properties = new HashMap<>();

    /**
     * Holds the name of current step being executed.
     * Helpful in logging and reporting.
     */
    @Getter
    @Setter
    private String currentStepName;

    @Getter
    @Setter
    private LinkedHashMap<String, Object> customConfigurationProperties = new LinkedHashMap<>();

    public void put(String name, Object value) {
        properties.put(name, value);
    }
    public Optional<Object> get(String name) {
        return Optional.of(properties.get(name));
    }

}
