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
package org.goodload.goodload.dsl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Multiple steps ({@link SequenceElement}s) can be grouped together.
 * It provides a nested structure to the scenario steps and can be used when one wants to treat several
 * steps as one. For example, say logging in to your website takes 3 steps, and you want to get
 * the average time taken to perform the login, you can group these steps as single 'Login' step,
 * and the report that will be generated will contain the average time overall for the group, as well as
 * the average time to execute each step separately.
 * <br>
 * <b>Scenario Structure</b>
 * <ul>
 *  <li>Single step</li>
 *  <li>Group 1</li>
 *  <ul>
 *      <li>Group 1 step 1</li>
 *      <li>Group 1 step 2</li>
 *      <li>Group 1 step 3</li>
 *      <li>Group 1 Subgroup 1</li>
 *      <ul>
 *          <li>G1 S1 step 1</li>
 *          <li>G1 S1 step 2</li>
 *      </ul>
 *      <li>Group 1 step 4</li>
 *  </ul>
 * </ul>
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Group extends Action {
    private String groupId;

    public Group(String name, SequenceElement... steps) {
        super("name", steps);
        this.groupId = createGroupId();
    }

    private String createGroupId() {
        return UUID.randomUUID().toString();
    }
}
