package org.divsgaur.goodload.dsl;

/**
 * Anything that can be executed.
 */
public interface Executable extends SequenceElement {
    void function(Session session) throws Exception;
}
