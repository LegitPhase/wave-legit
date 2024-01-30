/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package be.kod3ra.wave.utils;

import be.kod3ra.wave.Wave;
import be.kod3ra.wave.utils.PingUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class HighPing
extends BukkitRunnable {
    private static final int MAX_PING_THRESHOLD = 600;

    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            int ping = PingUtil.getPing(player);
            if (ping <= 600) continue;
            String debugInfo = String.valueOf("Ping: " + ping);
            String kickMessage = "Disconnected because of high ping, " + debugInfo;
            player.kickPlayer(kickMessage);
        }
    }

    public void startChecking() {
        this.runTaskTimer((Plugin)Wave.getInstance(), 0L, 100L);
    }
}

