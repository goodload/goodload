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

import lombok.Getter;
import lombok.Setter;
import org.goodload.goodload.criteria.Criteria;
import org.springframework.stereotype.Component;

import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Configuration parsed or generated from the actual user's input and configuration.
 * It contains objects generated based on the configured values to help in execution.
 * e.g. User provides path to simulation jar file, but we need the actual jar to
 * so we can store that here after loading it.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
@Component
@Getter
@Setter
public class ParsedUserArgs {
    /**
     * Criteria for goodload.fail-when property
     */
    private List<Criteria> failPassCriteria = new LinkedList<>();

    /**
     * The loader used to load classes from user's simulation jar file.
     * @since 1.0
     */
    private URLClassLoader userSimulationsClassLoader;

    /**
     * Thread pool to be used for execution of simulations.
     * @since 1.0
     */
    private ExecutorService simulationExecutorService;
}
