package org.divsgaur.goodload.dsl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Group extends Action {
    private String groupId;

    public Group(String name, SequenceElement... steps) {
        super("name", steps);
        this.groupId = createGroupId();
    }

    private String createGroupId() {
        return UUID.randomUUID().toString();
    }
}
