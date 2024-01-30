/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package be.kod3ra.wave.checks.impl.movement;

import be.kod3ra.wave.Wave;
import be.kod3ra.wave.checks.Check;
import be.kod3ra.wave.checks.impl.CheckInfo;
import be.kod3ra.wave.packet.WrappedPacket;
import be.kod3ra.wave.user.User;
import be.kod3ra.wave.user.engine.GroundEngine;
import be.kod3ra.wave.utils.CheckLogger;
import be.kod3ra.wave.utils.Latency;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@CheckInfo(name="FLIGHT")
public final class FlightD
extends Check {
    private long lastGroundedTime;
    private boolean isEnabled;
    private long violationsResetTime;
    private long lastResetTime = System.currentTimeMillis();
    private int maxViolations;
    private int groundTime;
    private String action;
    private long ignoreTime = 2000L;
    private long lastIgnoreTime;

    public FlightD() {
        FileConfiguration config = Wave.getInstance().getConfig();
        this.isEnabled = config.getBoolean("Checks.FlightD.ENABLED", true);
        this.maxViolations = config.getInt("Checks.FlightD.MAX-VIOLATIONS", 65);
        this.groundTime = config.getInt("Checks.FlightD.GROUND-TIME", 2750);
        this.action = config.getString("Checks.FlightD.ACTION", "kick %player% \u00a7b\u00a7lWave \u00a77\u00bb \u00a7eUnfair Advantage.");
        this.violationsResetTime = config.getLong("violations-reset", 120000L);
    }

    @Override
    public void onPacket(User user, WrappedPacket wrappedPacket) {
        if (!this.isEnabled) {
            return;
        }
        Player player = user.getPlayer();
        if (wrappedPacket.isFlying()) {
            if (player.getFallDistance() > 0.0f) {
                return;
            }
            if (player != null && (player.isOp() || player.getGameMode() == GameMode.CREATIVE || player.hasPermission("wave.bypass.flight"))) {
                return;
            }
            if (this.hasSolidBlockNearby(user.getPlayer().getLocation())) {
                this.lastIgnoreTime = System.currentTimeMillis();
                return;
            }
            if (this.isHighLatency(user.getPlayer())) {
                return;
            }
            if (GroundEngine.isServerOnGround(player)) {
                this.lastGroundedTime = System.currentTimeMillis();
            } else if (System.currentTimeMillis() - this.lastGroundedTime > (long)this.groundTime && System.currentTimeMillis() - this.lastIgnoreTime > this.ignoreTime) {
                ++this.violations;
                String debugInfo = String.valueOf("Last ground time: " + this.lastGroundedTime);
                this.flag(user, "D", "Is not on the ground for more than 2.75 seconds.", this.violations, debugInfo);
                if (player != null) {
                    CheckLogger.log(player.getName(), "FLIGHT", "Type: D Debug:" + debugInfo);
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

    private boolean hasSolidBlockNearby(Location location) {
        for (int x = -1; x <= 1; ++x) {
            for (int y = -1; y <= 1; ++y) {
                for (int z = -1; z <= 1; ++z) {
                    Block nearbyBlock = location.getBlock().getRelative(x, y, z);
                    Material blockType = nearbyBlock.getType();
                    if (this.isAir(blockType)) continue;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isAir(Material material) {
        return material == Material.AIR;
    }

    private boolean isHighLatency(Player player) {
        if (player == null) {
            return false;
        }
        int latency = Latency.getLag(player);
        return latency > 200;
    }
}

