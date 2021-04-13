package org.divsgaur.goodload.dsl;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Data
public class Group {
    private String name;
    private String id;

    public Group(String name) {
        this.name = name;
        this.id = createGroupId();
    }

    private String createGroupId() {
        return UUID.randomUUID().toString();
    }
}
