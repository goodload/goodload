package org.divsgaur.goodload.dsl;

import lombok.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    public void put(String name, Object value) {
        properties.put(name, value);
    }
    public Optional<Object> get(String name) {
        return Optional.of(properties.get(name));
    }
}
