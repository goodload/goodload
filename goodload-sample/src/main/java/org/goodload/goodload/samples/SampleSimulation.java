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

import java.util.Collections;
import java.util.List;

import static org.goodload.goodload.dsl.DSL.*;

public class SampleSimulation implements Simulation {
    @Override
    public List<Action> init() {
        Action scenario = scenario("Sample scenario",
                group("Login",
                        exec("Login: Exec1", session -> {}),
                        exec("Login: Exec2", session -> {}),
                        check(session -> true),
                        exec("Login: Exec3", session -> {})),
                check(session -> session.get("HEADER-AUTHENTICATION").orElse("").equals("401")),
                exec("Execution 1: ", session -> {}),
                group("Logout",
                        exec("Logout: Exec 1", session -> {}),
                        exec("Logout: Exec 2", session -> {}),
                        exec("Logout: Exec 3", session -> {}))
        );

        return Collections.singletonList(scenario);
    }
}
