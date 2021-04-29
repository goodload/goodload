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
package org.goodload.goodload.dsl;

/**
 * Functional interface that can be overridden to evaluate a condition and return a boolean value.
 * If the boolean value returned is false, the check is deemed to be failed and will be treated as
 * fail in simulation.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 *
 * @since 1.0
 */
public interface Check extends SequenceElement {
    /**
     * Implement this method to provide the logic for checking a condition.
     * @param session The simulation engine will provide a Session object containing information about the current
     *                iteration and information stored by any other steps that have been executed previously in the
     *                current iteration.
     * @return {@code true} if the conditions have passed, false if the conditions have failed. If any of the checks
     *                      return false, the group of steps and the simulation therefore, is treated as failed.
     *
     * @since 1.0
     */
    boolean condition(Session session);
}
