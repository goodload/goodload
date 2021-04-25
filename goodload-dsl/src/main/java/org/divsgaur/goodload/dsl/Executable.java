package org.divsgaur.goodload.dsl;

/**
 * Executable is any code that the user wants to execute as part of simulation step.
 * These can be network calls, calls to other functions, IO or anything else.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 */
public interface Executable extends SequenceElement {
    /**
     * Implement this method to provide the statements/code whose performance is to measured.
     * @param session A session object is provided by the engine during runtime and can be to store data related
     *                to current iteration and access that data in the steps that follow.
     *                It can be used to build complex logic for steps further down the line in execution sequence.
     * @throws Exception Allows the user code to throw any kind of exception. If any exception is thrown, the
     *          execution is treated as failed and the reports are generated accordingly.
     */
    void function(Session session) throws Exception;
}
