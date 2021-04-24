package org.divsgaur.goodload.dsl;

/**
 * A step that can be used by DSL modules to configure the execution.
 * This should not be used by end user.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 */
public interface Configurer extends SequenceElement {
    void configure(Session session, String simulationConfigFileContents);
}
