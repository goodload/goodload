/*
 * Copyright (C) 2021 Divyansh Shekhar Gaur
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
        var scenario1 = scenario("Sample scenario 1",
                group("Login",
                        exec("Get request", session -> http(session)
                                .post("https://www.facebook.com")
                                .header("AUTHENTICATION", "")
                                .header("X-Cache-Control", "")
                                .body(jsonBody(new Sample("sample name", "sample descr")))
                                .go()),
                        exec("sdf", session -> http(session)
                                .post("https://www.facebook.com")
                                .header("AUTHENTICATION", "")
                                .header("X-Cache-Control", "")
                                .body(jsonBody(new Sample("sample name", "sample descr")))
                                .go()),
                        check("Login check", session -> true)),
                group("Logout",
                        exec("Logout: Exec 1", session -> http(session)
                                .post("https://www.facebook.com")
                                .header("AUTHENTICATION", "")
                                .header("X-Cache-Control", "")
                                .body(jsonBody(new Sample("sample name", "sample descr")))
                                .go()),
                        exec("Logout: Exec 2", session -> http(session)
                                .post("https://www.facebook.com")
                                .header("AUTHENTICATION", "")
                                .header("X-Cache-Control", "")
                                .body(jsonBody(new Sample("sample name", "sample descr")))
                                .go()),
                        exec("Logout: Exec 3", session -> http(session)
                                .post("https://www.facebook.com")
                                .header("AUTHENTICATION", "")
                                .header("X-Cache-Control", "")
                                .body(jsonBody(new Sample("sample name", "sample descr")))
                                .go()))
        );
        var scenario2 = scenario("Sample scenario 2",
                group("Login",
                        exec("Get request", session -> http(session)
                                .post("https://www.facebook.com")
                                .header("AUTHENTICATION", "")
                                .header("X-Cache-Control", "")
                                .body(jsonBody(new Sample("sample name", "sample descr")))
                                .go()),
                        exec("sdf", session -> http(session)
                                .post("https://www.facebook.com")
                                .header("AUTHENTICATION", "")
                                .header("X-Cache-Control", "")
                                .body(jsonBody(new Sample("sample name", "sample descr")))
                                .go()),
                        check("Login check", session -> true)),
                group("Logout",
                        exec("Logout: Exec 1", session -> http(session)
                                .post("https://www.facebook.com")
                                .header("AUTHENTICATION", "")
                                .header("X-Cache-Control", "")
                                .body(jsonBody(new Sample("sample name", "sample descr")))
                                .go()),
                        exec("Logout: Exec 2", session -> http(session)
                                .post("https://www.facebook.com")
                                .header("AUTHENTICATION", "")
                                .header("X-Cache-Control", "")
                                .body(jsonBody(new Sample("sample name", "sample descr")))
                                .go()),
                        exec("Logout: Exec 3", session -> http(session)
                                .post("https://www.facebook.com")
                                .header("AUTHENTICATION", "")
                                .header("X-Cache-Control", "")
                                .body(jsonBody(new Sample("sample name", "sample descr")))
                                .go()))
        );

        return Arrays.asList(scenario1, scenario2);
    }
}
