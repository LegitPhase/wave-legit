package be.kod3ra.wave.utils;

public class MemoryUtil {
    public static long getMaxMemory() {
        long maxMemory = Runtime.getRuntime().maxMemory();
        return maxMemory / 0x100000L;
    }

    public static long getUsedMemory() {
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long usedMemory = totalMemory - freeMemory;
        return usedMemory / 0x100000L;
    }
}

