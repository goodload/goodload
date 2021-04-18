package org.divsgaur.goodload.dsl;

public class DSL {
    public static Action scenario(String name, SequenceElement... steps) {
        return new Action(name, steps);
    }

    public static Action exec(String name, Executable executable) {
        return new Action(name, executable);
    }

    public static Action check(String name, Check check) {
        return new Action(name, check);
    }
    public static Action check(Check check) {
        return check(null, check);
    }

    public static Action group(String name, SequenceElement... steps) {
        return new Action(name, steps);
    }
}
