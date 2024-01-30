/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.GameMode
 *  org.bukkit.Location
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
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@CheckInfo(name="BADPACKETS")
public final class BadPacketsC
extends Check {
    private long lastResetTime = System.currentTimeMillis();
    private boolean isEnabled;
    private long violationsResetTime;
    private String action;
    private int maxViolations;

    public BadPacketsC() {
        FileConfiguration config = Wave.getInstance().getConfig();
        this.isEnabled = config.getBoolean("Checks.BadPacketsC.ENABLED", true);
        this.maxViolations = config.getInt("Checks.BadPacketsC.MAX-VIOLATIONS", 60);
        this.action = config.getString("Checks.BadPacketsC.ACTION", "wavekick %player%");
        this.violationsResetTime = config.getLong("violations-reset", 120000L);
    }

    private double distance(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null) {
            return 0.0;
        }
        double dx = loc1.getX() - loc2.getX();
        double dy = loc1.getY() - loc2.getY();
        double dz = loc1.getZ() - loc2.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    @Override
    public void onPacket(User user, WrappedPacket wrappedPacket) {
        Player player = user.getPlayer();
        UserData userData = Wave.getInstance().getUserData();
        if (!this.isEnabled) {
            return;
        }
        if (player != null && (player.isOp() || player.getGameMode() == GameMode.CREATIVE || player.hasPermission("wave.bypass.badpackets"))) {
            return;
        }
        if (wrappedPacket.isDigging()) {
            WrapperPlayClientPlayerDigging blockPosition = new WrapperPlayClientPlayerDigging(wrappedPacket.getPacketReceiveEvent());
            WrapperPlayClientPlayerDigging blockAction = new WrapperPlayClientPlayerDigging(wrappedPacket.getPacketReceiveEvent());
            if (blockAction.getAction() != DiggingAction.START_DIGGING) {
                return;
            }
            Vector3i vector = blockPosition.getBlockPosition();
            Vector3d vectorD = new Vector3d(vector.getX(), vector.getY(), vector.getZ());
            Location location = new Location(vectorD, 0.0f, 0.0f);
            org.bukkit.Location bukkitLocation = user.getPlayer().getLocation();
            Location packetEventsLocation = new Location(bukkitLocation.getX(), bukkitLocation.getY(), bukkitLocation.getZ(), bukkitLocation.getYaw(), bukkitLocation.getPitch());
            if (this.isHighLatency(user.getPlayer())) {
                return;
            }
            if (location != null && this.distance(packetEventsLocation, location) > 7.0) {
                ++this.violations;
                SetbackEngine.performSetback(user.getPlayer());
                String debugInfo = "Location: " + location + " | Player Location: " + packetEventsLocation;
                this.flag(user, "C", "Invalid digging location packet. Distance > 6 blocks.", this.violations, debugInfo);
                if (player != null) {
                    CheckLogger.log(player.getName(), "BADPACKETS", "Type: C Debug:" + debugInfo);
                }
                if (this.violations >= this.maxViolations) {
                    try {
                        String playerAction = this.action.replace("%player%", user.getName());
                        Wave.getInstance().getServer().getScheduler().runTask((Plugin)Wave.getInstance(), () -> Wave.getInstance().getServer().dispatchCommand((CommandSender)Wave.getInstance().getServer().getConsoleSender(), playerAction));
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (System.currentTimeMillis() - this.lastResetTime > this.violationsResetTime) {
            this.violations = 0;
            this.lastResetTime = System.currentTimeMillis();
        }
    }

    private boolean isHighLatency(Player player) {
        if (player == null) {
            return false;
        }
        int latency = Latency.getLag(player);
        return latency > 200;
    }
}

