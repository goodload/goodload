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
package org.goodload.goodload.dsl;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Action is a group of steps to be executed sequentially.
 * The root group is called <b>scenario</b> while the other groups are called <b>groups</b>.
 * It is a list of sequence elements that are to be executed in sequence when a simulation is executed.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 *
 * @since 1.0
 */
@EqualsAndHashCode
public class Action implements SequenceElement {

    @Getter
    private final String name;

    private List<SequenceElement> executionSequence = new ArrayList<>();

    /**
     * Creates a blank Action object without any steps.
     * @param name The name by which to identify the action in the performance report.
     * @since 1.0
     */
    Action(String name) {
        this.name = name;
    }

    /**
     * Creates an Action with list of steps passes as sequenceElements.
     * @param name The name by which the Action will be identified
     * @param sequenceElements The steps to be executed as part of the Action.
     * @since 1.0
     */
    Action(String name, SequenceElement... sequenceElements) {
        this.name = name;
        this.executionSequence = Arrays.asList(sequenceElements);
    }

    /**
     * Returns a shallow copy of the execution sequence.
     * Modifying the returned list won't affect the sequence.
     * @return Shallow copy of the step sequence.
     * @since 1.0
     */
    public List<SequenceElement> getExecutionSequence() {
        return new ArrayList<>(executionSequence);
    }
}
