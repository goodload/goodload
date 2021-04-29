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
 * Executable is any code that the user wants to execute as part of simulation step.
 * These can be network calls, calls to other functions, IO or anything else.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
public interface Executable extends SequenceElement {
    /**
     * Implement this method to provide the statements/code whose performance is to measured.
     * @param session A session object is provided by the engine during runtime and can be to store data related
     *                to current iteration and access that data in the steps that follow.
     *                It can be used to build complex logic for steps further down the line in execution sequence.
     * @throws Exception Allows the user code to throw any kind of exception. If any exception is thrown, the
     *          execution is treated as failed and the reports are generated accordingly.
     * @since 1.0
     */
    void function(Session session) throws Exception;
}
