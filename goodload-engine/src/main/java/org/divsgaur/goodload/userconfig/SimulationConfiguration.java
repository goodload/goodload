package org.divsgaur.goodload.userconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SimulationConfiguration implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;

    @JsonProperty(value = "class")
    private String fullClassName;

    private int throughput;

    private int concurrency;

    private int iterations;

    @JsonProperty(value = "hold-for")
    private String holdFor;

    @JsonProperty(value = "ramp-up")
    private String rampUp;

    @JsonProperty(value = "ramp-down")
    private String rampDown;
}
