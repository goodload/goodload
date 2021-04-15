package org.divsgaur.goodload.dsl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.divsgaur.goodload.core.Mutable;

import java.util.Arrays;
import java.util.List;

@EqualsAndHashCode
public class Action implements SequenceElement {
    private Mutable<Session> session;

    @Getter
    private final String name;

    /**
     * Multiple actions can be grouped together.
     * Actions not explicitly in a group have group ID of their own.
     */
    @Getter
    @Setter
    private Group group;

    private List<SequenceElement> executionSequence;

    Action(String name) {
        this.name = name;
        this.group = new Group(name);
    }

    Action(String name, Group group, Session session) {
        this.name = name;
        this.group = group;
        this.session = new Mutable<>(session);
    }

    Action(String name, SequenceElement... sequenceElements) {
        this.name = name;
        this.executionSequence = Arrays.asList(sequenceElements);
    }

    public Action exec(String name, Executable... executables) {
        this.executionSequence.addAll(Arrays.asList(executables));
        return this;
    }

    public Action check(Check check) {
        this.executionSequence.add(check);
        return this;
    }

    public Action group(String groupName, Action... actions) {
        Group group = new Group(groupName);
        group(group, actions);
        return this;
    }

    private void group(Group group, Action... actions) {
        for(Action action : actions) {
            action.setGroup(group);
        }
        this.executionSequence.addAll(Arrays.asList(actions));
    }
}
