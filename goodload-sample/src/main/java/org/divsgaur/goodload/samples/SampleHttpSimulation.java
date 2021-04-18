package org.divsgaur.goodload.samples;

import org.divsgaur.goodload.dsl.Action;
import org.divsgaur.goodload.dsl.Simulation;
import org.divsgaur.goodload.samples.data.Sample;

import java.util.Collections;
import java.util.List;

import static org.divsgaur.goodload.dsl.DSL.*;
import static org.divsgaur.goodload.http.HttpDSL.http;
import static org.divsgaur.goodload.http.HttpDSL.jsonBody;

public class SampleHttpSimulation extends Simulation {
    @Override
    public List<Action> init() {
        Action scenario = scenario("Sample scenario",
                group("Login",
                        exec("Get request", (session) -> http()
                                .post("https://www.google.com")
                                .header("AUTHENTICATION", "")
                                .header("X-Cache-Control", "")
                                .body(jsonBody(new Sample("sample name", "sample descr")))
                                .go()),
                        exec("sdf", (session) -> {}),
                        check((session) -> true)),
                /*.check((session) -> {
                    return ((String) session.get("HEADER-AUTHENTICATION")).equals("401");
                })*/
                exec("Execution 1: ", (session) -> {String random = "Some random execution";}),
                group("Logout",
                        exec("Logout: Exec 1", (session) -> {}),
                        exec("Logout: Exec 2", (session) -> {}),
                        exec("Logout: Exec 3", (session) -> {}))
        );

        return Collections.singletonList(scenario);
    }
}
