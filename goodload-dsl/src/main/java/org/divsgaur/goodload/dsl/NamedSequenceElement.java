package org.divsgaur.goodload.dsl;

public class NamedSequenceElement<T extends SequenceElement> implements SequenceElement {
    private String name;
    private T sequenceElement;

    public NamedSequenceElement(String name, T sequenceElement) {
        this.name = name;
        this.sequenceElement = sequenceElement;
    }
}
