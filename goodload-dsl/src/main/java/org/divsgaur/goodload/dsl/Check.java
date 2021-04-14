package org.divsgaur.goodload.dsl;

public interface Check extends SequenceElement {
    boolean condition(Session session);
}
