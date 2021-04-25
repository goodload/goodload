package org.divsgaur.goodload.dsl;

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
