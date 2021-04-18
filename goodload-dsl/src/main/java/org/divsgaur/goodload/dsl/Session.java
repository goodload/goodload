package org.divsgaur.goodload.dsl;

import lombok.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Session {
    private Map<String, Object> properties = new HashMap<>();

    public void put(String name, Object value) {
        properties.put(name, value);
    }
    public Optional<Object> get(String name) {
        return Optional.of(properties.get(name));
    }
}
