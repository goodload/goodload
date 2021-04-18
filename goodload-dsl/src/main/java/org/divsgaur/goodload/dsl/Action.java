package org.divsgaur.goodload.dsl;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@EqualsAndHashCode
public class Action implements SequenceElement {

    @Getter
    private final String name;

    private List<SequenceElement> executionSequence = new ArrayList<>();

    Action(String name) {
        this.name = name;
    }

    Action(String name, SequenceElement... sequenceElements) {
        this.name = name;
        this.executionSequence = Arrays.asList(sequenceElements);
    }

    /**
     * Returns a shallow copy of the execution sequence.
     * Modifying the returned list won't affect the sequence.
     * @return Shallow copy of the step sequence.
     */
    public List<SequenceElement> getExecutionSequence() {
        return new ArrayList<>(executionSequence);
    }
}
