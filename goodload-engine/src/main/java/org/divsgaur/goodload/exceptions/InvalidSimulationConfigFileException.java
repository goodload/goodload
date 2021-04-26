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
package org.divsgaur.goodload.exceptions;

import java.io.IOException;

/**
 * Thrown when the simulation config YAML file couldn't be read/found or the contents were invalid.
 * e.g. missing file, permissions issue, syntax error, missing required fields, etc.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
public class InvalidSimulationConfigFileException extends IOException {
    public InvalidSimulationConfigFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
