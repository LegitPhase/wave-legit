package be.kod3ra.wave.utils;

import be.kod3ra.wave.Wave;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class HighPing
        extends BukkitRunnable {
    private static final int MAX_PING_THRESHOLD = 600;

    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            int ping = PingUtil.getPing(player);
            if (ping <= 600) continue;
            String debugInfo = "Ping: " + ping;
            String kickMessage = "Disconnected because of high ping, " + debugInfo;
            player.kickPlayer(kickMessage);
        }
    }

    public void startChecking() {
        this.runTaskTimer(Wave.getInstance(), 0L, 100L);
    }
}

