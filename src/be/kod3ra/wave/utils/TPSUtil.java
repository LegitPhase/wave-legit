/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 */
package be.kod3ra.wave.utils;

import org.bukkit.Bukkit;

public class TPSUtil {
    public static double[] getRecentTPS() {
        try {
            Object minecraftServer = Bukkit.getServer().getClass().getDeclaredMethod("getServer", new Class[0]).invoke(Bukkit.getServer(), new Object[0]);
            double[] recentTps = (double[])minecraftServer.getClass().getField("recentTps").get(minecraftServer);
            for (int i = 0; i < recentTps.length; ++i) {
                recentTps[i] = Math.min(recentTps[i], 20.0);
            }
            return recentTps;
        }
        catch (Exception e) {
            e.printStackTrace();
            return new double[]{-1.0, -1.0, -1.0};
        }
    }
}

