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
package org.goodload.goodload.exceptions;

import org.goodload.goodload.dsl.Action;

/**
 * Thrown when a check step in a scenario fails.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
public class CheckFailedException extends RuntimeException {

    /**
     * Creates an instane of CheckFailedException.
     * @param simulationName The name of the simulation that failed.
     * @param action The name of the check step that failed.
     *               Note that a check is encapsulated by a single step Action object
     *               which provides the check a name.
     */
    public CheckFailedException(String simulationName, Action action) {
        super(String.format("Check failed for simulation `%s` action `%s`",
                simulationName,
                action.getName()));
    }
}
