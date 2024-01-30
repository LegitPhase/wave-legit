/*
 * Decompiled with CFR 0.152.
 */
package be.kod3ra.wave.utils;

public class TimeUtil {
    public static long nowlong() {
        return System.currentTimeMillis();
    }

    public static boolean elapsed(long from, long required) {
        return System.currentTimeMillis() - from > required;
    }
}

