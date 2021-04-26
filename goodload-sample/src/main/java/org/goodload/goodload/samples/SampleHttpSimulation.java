/*
Copyright (C) 2021 Goodload

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.goodload.goodload.samples;

import org.goodload.goodload.dsl.Action;
import org.goodload.goodload.dsl.Simulation;
import org.goodload.goodload.samples.data.Sample;

import java.util.Arrays;
import java.util.List;

import static org.goodload.goodload.dsl.DSL.*;
import static org.goodload.goodload.http.HttpDSL.http;
import static org.goodload.goodload.http.HttpDSL.jsonBody;

public class SampleHttpSimulation implements Simulation {
    @Override
    public List<Action> init() {
        Action scenario = scenario("Sample scenario",
                group("Login",
                        exec("Get request", session -> http(session)
                                .post("https://www.google.com")
                                .header("AUTHENTICATION", "")
                                .header("X-Cache-Control", "")
                                .body(jsonBody(new Sample("sample name", "sample descr")))
                                .go()),
                        exec("sdf", session -> { }),
                        check("Login check", session -> true)),
                /*.check((session) -> {
                    return ((String) session.get("HEADER-AUTHENTICATION")).equals("401");
                })*/
                exec("Execution 1: ", session -> {}),
                group("Logout",
                        exec("Logout: Exec 1", session -> {}),
                        exec("Logout: Exec 2", session -> {}),
                        exec("Logout: Exec 3", session -> {}))
        );
        var scenario2 = scenario("Sample scenario",
                group("Login",
                        exec("Get request", session -> http(session)
                                .post("https://www.google.com")
                                .header("AUTHENTICATION", "")
                                .header("X-Cache-Control", "")
                                .body(jsonBody(new Sample("sample name", "sample descr")))
                                .go()),
                        exec("sdf", session -> { }),
                        check("Login check", session -> true)),
                group("Logout",
                        exec("Logout: Exec 1", session -> {}),
                        exec("Logout: Exec 2", session -> {}),
                        exec("Logout: Exec 3", session -> {}))
        );

        return Arrays.asList(scenario, scenario2);
    }
}
