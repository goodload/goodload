package org.divsgaur.goodload.internal;

import java.util.Date;

/**
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 */
public class Util {
    public static long currentTimestamp() {
        return new Date().getTime();
    }
}