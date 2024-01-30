/*
 * Decompiled with CFR 0.152.
 */
package be.kod3ra.wave.user.utilsengine;

import be.kod3ra.wave.utils.MemoryUtil;

public class ReliabilityEngine {
    public static double calculateReliability(int ping, double tps) {
        double normalizedPing = ReliabilityEngine.normalizeValue(ping, 0.0, 200.0, 100.0, 0.0);
        double normalizedTps = ReliabilityEngine.normalizeValue(tps, 15.0, 20.0, 0.0, 100.0);
        long maxMemoryGB = MemoryUtil.getMaxMemory() / 1024L;
        double normalizedMemory = ReliabilityEngine.normalizeMemory(maxMemoryGB);
        double reliability = normalizedTps * 0.7 + normalizedPing * 0.2 + normalizedMemory * 0.1;
        reliability = Math.max(reliability, 0.0);
        try {
            return Double.parseDouble(String.format("%.1f", reliability));
        } catch (NumberFormatException e) {
            return Double.parseDouble(String.format("%.0f", reliability));
        }
    }

    private static double normalizeMemory(long maxMemoryGB) {
        return ReliabilityEngine.normalizeValue(maxMemoryGB, 1.0, 16.0, 0.0, 100.0);
    }

    private static double normalizeValue(double value, double minInput, double maxInput, double minOutput, double maxOutput) {
        return (value - minInput) / (maxInput - minInput) * (maxOutput - minOutput) + minOutput;
    }
}

