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
 * @since 1.0
 */
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Session {
    private final Map<String, Object> properties = new HashMap<>();

    /**
     * Holds the name of current step being executed.
     * Helpful in logging and reporting.
     * @since 1.0
     */
    @Getter
    @Setter
    private String currentStepName;

    /**
     * The custom configuration properties provided in user config under prefix goodload.custom.
     */
    @Getter
    @Setter
    private LinkedHashMap<String, Object> customConfigurationProperties = new LinkedHashMap<>();

    /**
     * Save a key-value pair in the current session.
     * @param name The name/key/identifier of the object to save.
     * @param value The object to save.
     */
    public void put(String name, Object value) {
        properties.put(name, value);
    }

    /**
     * Retrieve an object from the current session.
     * @param name The key to search in the session.
     * @return An Optional containing the required object if the key was found, otherwise empty Optional.
     */
    public Optional<Object> get(String name) {
        return Optional.of(properties.get(name));
    }

}
