/*
 * Copyright (C) 2021 Divyansh Shekhar Gaur
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
package org.goodload.goodload.dsl;

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
