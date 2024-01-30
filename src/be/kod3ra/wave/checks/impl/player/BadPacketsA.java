/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.bukkit.GameMode
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package be.kod3ra.wave.checks.impl.player;

import be.kod3ra.wave.Wave;
import be.kod3ra.wave.checks.Check;
import be.kod3ra.wave.checks.impl.CheckInfo;
import be.kod3ra.wave.packet.WrappedPacket;
import be.kod3ra.wave.user.User;
import be.kod3ra.wave.user.UserData;
import be.kod3ra.wave.user.utilsengine.SetbackEngine;
import be.kod3ra.wave.utils.CheckLogger;
import be.kod3ra.wave.utils.Latency;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.LinkedList;

@CheckInfo(name = "BADPACKETS")
public final class BadPacketsA
        extends Check {
    private static final int MAX_HISTORY_SIZE = 20000;
    private final LinkedList<Long> packetTimes = new LinkedList();
    private final boolean isEnabled;
    private final int maxPacketsPerSecond;
    private final long violationsResetTime;
    private final int maxViolations;
    private final String action;
    private long lastResetTime = System.currentTimeMillis();

    public BadPacketsA() {
        FileConfiguration config = Wave.getInstance().getConfig();
        this.isEnabled = config.getBoolean("Checks.BadPacketsA.ENABLED", true);
        this.maxPacketsPerSecond = config.getInt("Checks.BadPacketsA.MAX-PACKETS-PER-SECOND", 100);
        this.maxViolations = config.getInt("Checks.BadPacketsA.MAX-VIOLATIONS", 40);
        this.action = config.getString("Checks.BadPacketsA.ACTION", "wavekick %player%");
        this.violationsResetTime = config.getLong("violations-reset", 120000L);
    }

    @Override
    public void onPacket(User user, WrappedPacket wrappedPacket) {
        if (this.isEnabled && wrappedPacket.allPackets()) {
            this.packetTimes.add(System.currentTimeMillis());
            Player player = user.getPlayer();
            UserData userData = Wave.getInstance().getUserData();
            while (this.packetTimes.size() > 20000) {
                this.packetTimes.removeFirst();
            }
            if (player != null && (player.isOp() || player.getGameMode() == GameMode.CREATIVE || player.hasPermission("wave.bypass.badpackets"))) {
                return;
            }
            if (this.isHighLatency(user.getPlayer())) {
                return;
            }
            int packetsPerSecond = this.calculatePacketsPerSecond();
            if (packetsPerSecond > this.maxPacketsPerSecond) {
                String debugInfo = "Packets per second: " + packetsPerSecond;
                this.flag(user, "A", "High packet rate", this.violations, debugInfo);
                ++this.violations;
                SetbackEngine.performSetback(user.getPlayer());
                if (player != null) {
                    CheckLogger.log(player.getName(), "BADPACKETS", "Type: A Debug:" + debugInfo);
                }
                if (this.violations >= this.maxViolations && this.violations >= this.maxViolations) {
                    try {
                        String playerAction = this.action.replace("%player%", user.getName());
                        Wave.getInstance().getServer().getScheduler().runTask(Wave.getInstance(), () -> Wave.getInstance().getServer().dispatchCommand(Wave.getInstance().getServer().getConsoleSender(), playerAction));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (System.currentTimeMillis() - this.lastResetTime > this.violationsResetTime) {
                this.violations = 0;
                this.lastResetTime = System.currentTimeMillis();
            }
        }
    }

    private int calculatePacketsPerSecond() {
        long oldestTime;
        long currentTime = System.currentTimeMillis();
        long l = oldestTime = this.packetTimes.size() > 0 ? this.packetTimes.getFirst() : currentTime;
        while (!this.packetTimes.isEmpty() && currentTime - oldestTime > 1000L) {
            this.packetTimes.removeFirst();
            oldestTime = this.packetTimes.size() > 0 ? this.packetTimes.getFirst() : currentTime;
        }
        return this.packetTimes.size();
    }

    private boolean isHighLatency(Player player) {
        if (player == null) {
            return false;
        }
        int latency = Latency.getLag(player);
        return latency > 200;
    }
}

