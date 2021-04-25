package org.divsgaur.goodload.samples;

import org.divsgaur.goodload.dsl.Action;
import org.divsgaur.goodload.dsl.Simulation;

import java.util.Collections;
import java.util.List;

import static org.divsgaur.goodload.dsl.DSL.*;

public class SampleSimulation extends Simulation {
    @Override
    public List<Action> init() {
        Action scenario = scenario("Sample scenario",
                group("Login",
                        exec("Login: Exec1", session -> {}),
                        exec("Login: Exec2", session -> {}),
                        check(session -> true),
                        exec("Login: Exec3", session -> {})),
                check(session -> {
                    return ((String) session.get("HEADER-AUTHENTICATION").orElse("")).equals("401");
                }),
                exec("Execution 1: ", session -> {String random = "Some random execution";}),
                group("Logout",
                        exec("Logout: Exec 1", session -> {}),
                        exec("Logout: Exec 2", session -> {}),
                        exec("Logout: Exec 3", session -> {}))
        );

        return Collections.singletonList(scenario);
    }
}
