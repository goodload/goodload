package org.divsgaur.goodload.internal;

import java.time.Duration;
import java.util.Date;

/**
 * Contains utility methods for things repeatedly used across the project.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 */
public class Util {
    public static long currentTimestamp() {
        return new Date().getTime();
    }

    public static long parseDurationToMillis(String timePeriod) {
        var duration = Duration.parse("PT" + timePeriod);
        return duration.toMillis();
    }
}
