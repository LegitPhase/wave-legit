/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.bukkit.GameMode
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package be.kod3ra.wave.checks.impl.movement;

import be.kod3ra.wave.Wave;
import be.kod3ra.wave.checks.Check;
import be.kod3ra.wave.checks.impl.CheckInfo;
import be.kod3ra.wave.packet.WrappedPacket;
import be.kod3ra.wave.user.User;
import be.kod3ra.wave.user.UserData;
import be.kod3ra.wave.user.engine.MovementEngine;
import be.kod3ra.wave.utils.CheckLogger;
import be.kod3ra.wave.utils.Latency;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@CheckInfo(name = "FLIGHT")
public final class FlightB
        extends Check {
    private final MovementEngine movementEngine = new MovementEngine();
    private final boolean isEnabled;
    private final double maxVerticalSpeed;
    private final int onJoinDisabledTime;
    private final int maxViolations;
    private final long violationsResetTime;
    private long lastResetTime = System.currentTimeMillis();
    private final String action;

    public FlightB() {
        this.isEnabled = Wave.getInstance().getConfig().getBoolean("Checks.FlightB.ENABLED", true);
        this.maxVerticalSpeed = Wave.getInstance().getConfig().getDouble("Checks.FlightB.MAX-VERTICAL-SPEED", 3.8);
        this.onJoinDisabledTime = Wave.getInstance().getConfig().getInt("Checks.FlightB.ON-JOIN-DISABLED-TIME", 5);
        this.maxViolations = Wave.getInstance().getConfig().getInt("Checks.FlightB.MAX-VIOLATIONS", 10);
        this.action = Wave.getInstance().getConfig().getString("Checks.FlightB.ACTION", "wavekick %player%");
        this.violationsResetTime = Wave.getInstance().getConfig().getLong("violations-reset", 120000L);
    }

    @Override
    public void onPacket(User user, WrappedPacket wrappedPacket) {
        if (this.isEnabled) {
            Player player = user.getPlayer();
            UserData userData = Wave.getInstance().getUserData();
            long joinTime = userData.getJoinTime(player.getUniqueId());
            if (System.currentTimeMillis() - joinTime < (long) (this.onJoinDisabledTime * 1000L)) {
                return;
            }
            if (player != null && (player.isOp() || player.getGameMode() == GameMode.CREATIVE || player.hasPermission("wave.bypass.flight"))) {
                return;
            }
            if (this.isHighLatency(user.getPlayer())) {
                return;
            }
            if (wrappedPacket.isFlying()) {
                this.movementEngine.updateCoordinates(wrappedPacket);
                double deltaY = this.movementEngine.getDeltaY();
                String debugInfo = String.valueOf(deltaY);
                if (deltaY > 20.0) {
                    return;
                }
                if (deltaY > this.maxVerticalSpeed) {
                    ++this.violations;
                    if (this.violations >= this.maxViolations) {
                        try {
                            String playerAction = this.action.replace("%player%", user.getName());
                            Wave.getInstance().getServer().getScheduler().runTask(Wave.getInstance(), () -> Wave.getInstance().getServer().dispatchCommand(Wave.getInstance().getServer().getConsoleSender(), playerAction));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    this.flag(user, "B", "High Y speed", this.violations, debugInfo);
                    if (player != null) {
                        CheckLogger.log(player.getName(), "FLIGHT", "Type: B Debug:" + debugInfo);
                    }
                }
            }
            if (System.currentTimeMillis() - this.lastResetTime > this.violationsResetTime) {
                this.violations = 0;
                this.lastResetTime = System.currentTimeMillis();
            }
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

