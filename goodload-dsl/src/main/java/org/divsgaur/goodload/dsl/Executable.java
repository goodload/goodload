package org.divsgaur.goodload.dsl;

import java.util.UUID;

/**
 * Anything that can be executed.
 */
public interface Executable extends SequenceElement {
    String name = generateRandomName();
    void function(Session session);

    static String generateRandomName() {
        return new String("Unnamed executable " + UUID.randomUUID().clockSequence());
    }
}
