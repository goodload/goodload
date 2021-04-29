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
package org.goodload.goodload.userconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Contents of the simulation configuration YAML file passed by user.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
@Data
@JsonTypeName(value = "goodload")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class GoodloadUserConfigurationProperties implements Serializable {
    public static final long serialVersionUID = 1L;

    /**
     * List of executions and their configuration.
     * @since 1.0
     */
    @JsonProperty(value="simulations")
    private List<SimulationConfiguration> simulations;

    /**
     * Define when the aggregate report for a given step/group/scenario should fail depending
     * upon how much of the iteration for that step/group/scenario fails.
     * @since 1.0
     */
    @JsonProperty(value="fail-when")
    private List<String> failPassCriteria = new ArrayList<>();

    /**
     * Properties and configuration that affect how the reports are generated.
     * @since 1.0
     */
    @JsonProperty(value="reporting")
    private ReportingConfiguration reporting = new ReportingConfiguration();

    /**
     * Allows the user to define some properties that they require.
     * These are not validated by goodload.
     * @since 1.0
     */
    @JsonProperty(value="custom")
    private LinkedHashMap<String, Object> custom;
}
