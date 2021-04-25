package org.divsgaur.goodload.userconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * The configuration properties relating to each simulation the user wants to run.
 */
@Data
public class SimulationConfiguration implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Name of the simulation.
     */
    private String name;

    /**
     * The fully classified classname of the class in which the simultion has been defined.
     */
    @JsonProperty(value = "class")
    private String fullClassName;

    /**
     * The maximum number of iterations of the simulation per second.
     * Note that it is not the overall throughput, but the throughput per thread.
     * If the concurrency is 4 and throughput is 10, then the actual overall throughput will be
     * 4*10 = 40.
     */
    private Integer throughput;

    /**
     * The maximum number of parallel execution of the simulation.
     * If the concurrency is 4 and throughput is 10, then the actual overall throughput will be 4*10 = 40.
     * It means that there will be 4 threads running in parallel, each thread iterating over the simulation
     * 10 times in a second.
     */
    private int concurrency;

    /**
     * The maximum number of iterations the simulation will go through in each thread.
     * If the iteration = 10 and concurrency = 4, then total number of iterations of the simulation should be
     * 10 * 4 = 40.
     */
    private Integer iterations;

    /**
     * The duration for which the simulation will be executed.
     * New iterations will be started and executed until the duration is over.
     * It is simply to a while loop until duration exceeded.
     * If the number of iterations have also been defined, then the iterations will stop
     * if either the duration is over or the iteration count is reached (whichever happens first)
     */
    @JsonProperty(value = "hold-for")
    private String holdFor;

    /**
     * <b>Not yet supported. Reserved for future.</b>
     * How much time should it take to reach from 0 overall throughput to maximum overall throughput.
     */
    @JsonProperty(value = "ramp-up")
    private String rampUp;

    /**
     * <b>Not yet supported. Reserved for future.</b>
     * How much time should it take to reach from maximum overall throughput to 0.
     */
    @JsonProperty(value = "ramp-down")
    private String rampDown;

    /**
     * Enable or disable a simulation.
     * The simulation is executed only if this value is set to true,
     * otherwise it is ignored.
     * {@code true} by default.
     */
    private boolean enabled = true;
}
