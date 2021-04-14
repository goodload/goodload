package org.divsgaur.goodload.dsl;

public class DSL {
    public static Action scenario(String name) {
        return new Action(name);
    }

    public static Action exec(String name, Executable executable) {
        return new Action(name, executable);
    }

    public static Action check(String name, Check check) {
        return new Action(name, check);
    }

    public static Action check(Check check) {
        return check("", check);
    }
}
