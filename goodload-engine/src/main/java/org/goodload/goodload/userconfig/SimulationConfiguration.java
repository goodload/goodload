/*
 * Copyright (C) 2021 Goodload
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
package org.goodload.goodload.userconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * The configuration properties relating to each simulation the user wants to run.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
@Data
public class SimulationConfiguration implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Name of the simulation.
     * @since 1.0
     */
    private String name;

    /**
     * The fully classified classname of the class in which the simultion has been defined.
     * @since 1.0
     */
    @JsonProperty(value = "class")
    private String fullClassName;

    /**
     * The maximum number of iterations of the simulation per second.
     * Note that it is not the overall throughput, but the throughput per thread.
     * If the concurrency is 4 and throughput is 10, then the actual overall throughput will be
     * 4*10 = 40.
     * @since 1.0
     */
    private Integer throughput;

    /**
     * The maximum number of parallel execution of the simulation.
     * If the concurrency is 4 and throughput is 10, then the actual overall throughput will be 4*10 = 40.
     * It means that there will be 4 threads running in parallel, each thread iterating over the simulation
     * 10 times in a second.
     * @since 1.0
     */
    private int concurrency;

    /**
     * The maximum number of iterations the simulation will go through in each thread.
     * If the iteration = 10 and concurrency = 4, then total number of iterations of
     * the simulation should be 10 * 4 = 40.
     * @since 1.0
     */
    private Integer iterations;

    /**
     * The duration for which the simulation will be executed.
     * New iterations will be started and executed until the duration is over.
     * It is simply to a while loop until duration exceeded.
     * If the number of iterations have also been defined, then the iterations will stop
     * if either the duration is over or the iteration count is reached (whichever happens first)
     * @since 1.0
     */
    @JsonProperty(value = "hold-for")
    private String holdFor;

    /**
     * <b>Not yet supported. Reserved for future.</b>
     * How much time should it take to reach from 0 overall throughput to maximum overall throughput.
     * @since 1.0
     */
    @JsonProperty(value = "ramp-up")
    private String rampUp;

    /**
     * <b>Not yet supported. Reserved for future.</b>
     * How much time should it take to reach from maximum overall throughput to 0.
     * @since 1.0
     */
    @JsonProperty(value = "ramp-down")
    private String rampDown;

    /**
     * Enable or disable a simulation.
     * The simulation is executed only if this value is set to true,
     * otherwise it is ignored.
     * {@code true} by default.
     * @since 1.0
     */
    private boolean enabled = true;
}
