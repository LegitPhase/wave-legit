/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.util.Vector
 */
package be.kod3ra.wave.checks.impl.movement;

import be.kod3ra.wave.Wave;
import be.kod3ra.wave.checks.Check;
import be.kod3ra.wave.checks.impl.CheckInfo;
import be.kod3ra.wave.packet.WrappedPacket;
import be.kod3ra.wave.user.User;
import be.kod3ra.wave.user.UserData;
import be.kod3ra.wave.user.engine.FlightEngine;
import be.kod3ra.wave.user.utilsengine.LocationEngine;
import be.kod3ra.wave.user.utilsengine.SetbackEngine;
import be.kod3ra.wave.utils.CheckLogger;
import be.kod3ra.wave.utils.Latency;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

@CheckInfo(name="FLIGHT")
public final class FlightA
extends Check {
    private FlightEngine flightEngine = new FlightEngine();
    private LocationEngine locationEngine = new LocationEngine();
    private boolean isEnabled;
    private int onJoinDisabledTime;
    private long violationsResetTime;
    private int maxViolations;
    private String action;
    private final int SLIME_IGNORE_RADIUS = 5;
    private long lastResetTime = System.currentTimeMillis();

    public FlightA() {
        FileConfiguration config = Wave.getInstance().getConfig();
        this.isEnabled = config.getBoolean("Checks.FlightA.ENABLED", true);
        this.onJoinDisabledTime = config.getInt("Checks.FlightA.ON-JOIN-DISABLED-TIME", 3);
        this.maxViolations = config.getInt("Checks.FlightA.MAX-VIOLATIONS", 20);
        this.action = config.getString("Checks.FlightA.ACTION", "kick %player% \u00a7b\u00a7lWave \u00a77\u00bb \u00a7eUnfair Advantage.");
        this.violationsResetTime = config.getLong("violations-reset", 120000L);
    }

    @Override
    public void onPacket(User user, WrappedPacket wrappedPacket) {
        if (!this.isEnabled) {
            return;
        }
        Player player = user.getPlayer();
        UserData userData = Wave.getInstance().getUserData();
        if (player != null && System.currentTimeMillis() - userData.getJoinTime(player.getUniqueId()) > (long)(this.onJoinDisabledTime * 1000)) {
            if (System.currentTimeMillis() - userData.getLastTeleportTime(player.getUniqueId()) < 3000L) {
                return;
            }
            if (player != null && (player.isOp() || player.getGameMode() == GameMode.CREATIVE || player.hasPermission("wave.bypass.flight"))) {
                return;
            }
            if (wrappedPacket.isFlying()) {
                Location currentLocation = this.locationEngine.getCurrentLocation(player);
                Location previousGroundLocation = this.locationEngine.getPreviousGroundLocation();
                boolean isFlying = this.flightEngine.isFlying(wrappedPacket);
                if (previousGroundLocation == null || currentLocation == null) {
                    this.locationEngine.updatePreviousLocations(player);
                    return;
                }
                if (player.getFallDistance() > 0.0f) {
                    return;
                }
                if (this.isHighLatency(user.getPlayer())) {
                    return;
                }
                Vector velocity = player.getVelocity();
                if (velocity.getY() > -1.0) {
                    return;
                }
                if (this.hasSolidBlockNearby(player, currentLocation, 5)) {
                    return;
                }
                if (isFlying) {
                    ++this.violations;
                    SetbackEngine.performSetback(user.getPlayer());
                    if (this.violations >= this.maxViolations) {
                        try {
                            String playerAction = this.action.replace("%player%", user.getName());
                            Wave.getInstance().getServer().getScheduler().runTask((Plugin)Wave.getInstance(), () -> Wave.getInstance().getServer().dispatchCommand((CommandSender)Wave.getInstance().getServer().getConsoleSender(), playerAction));
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    String debugInfo = "No debug for FlightA";
                    this.flag(user, "A", "Flying in the air", this.violations, debugInfo);
                    if (player != null) {
                        CheckLogger.log(player.getName(), "FLIGHT", "Type: A Debug:" + debugInfo);
                    }
                }
            }
            if (System.currentTimeMillis() - this.lastResetTime > this.violationsResetTime) {
                this.violations = 0;
                this.lastResetTime = System.currentTimeMillis();
            }
        }
    }

    private boolean hasSolidBlockNearby(Player player, Location location, int radius) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        World world = location.getWorld();
        for (int offsetX = -radius; offsetX <= radius; ++offsetX) {
            for (int offsetY = -radius; offsetY <= radius; ++offsetY) {
                for (int offsetZ = -radius; offsetZ <= radius; ++offsetZ) {
                    Block block = world.getBlockAt((int)x + offsetX, (int)y + offsetY, (int)z + offsetZ);
                    if (!this.isSolidBlock(block)) continue;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isSolidBlock(Block block) {
        return block.getType().isSolid() || block.isLiquid();
    }

    private boolean isHighLatency(Player player) {
        if (player == null) {
            return false;
        }
        int latency = Latency.getLag(player);
        return latency > 200;
    }
}

