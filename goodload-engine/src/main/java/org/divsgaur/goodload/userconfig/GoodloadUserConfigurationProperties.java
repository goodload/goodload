package org.divsgaur.goodload.userconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Contents of the simulation configuration YAML file passed by user.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 */
@Data
@JsonTypeName(value = "goodload")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class GoodloadUserConfigurationProperties implements Serializable {
    public static final long serialVersionUID = 1L;

    /**
     * List of executions and their configuration.
     */
    @JsonProperty(value="simulations")
    private List<SimulationConfiguration> simulations;

    /**
     * Properties and configuration that affect how the reports are generated.
     */
    @JsonProperty(value="reporting")
    private ReportingConfiguration reporting = new ReportingConfiguration();

    /**
     * Allows the user to define some properties that they require.
     * These are not validated by goodload.
     */
    @JsonProperty(value="custom")
    private HashMap<String, Object> custom;
}
