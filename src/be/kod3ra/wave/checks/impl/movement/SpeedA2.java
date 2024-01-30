package be.kod3ra.wave.checks.impl.movement;

import be.kod3ra.wave.Wave;
import be.kod3ra.wave.checks.Check;
import be.kod3ra.wave.checks.impl.CheckInfo;
import be.kod3ra.wave.packet.WrappedPacket;
import be.kod3ra.wave.user.User;
import be.kod3ra.wave.user.UserData;
import be.kod3ra.wave.user.engine.MovementEngine;
import be.kod3ra.wave.user.utilsengine.LocationEngine;
import be.kod3ra.wave.user.utilsengine.SetbackEngine;
import be.kod3ra.wave.utils.CheckLogger;
import be.kod3ra.wave.utils.Latency;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

@CheckInfo(name = "SPEED")
public final class SpeedA2
        extends Check {
    private final MovementEngine movementEngine = new MovementEngine();
    private final LocationEngine locationEngine = new LocationEngine();
    private final boolean isEnabled;
    private final double maxValue;
    private final long violationsResetTime;
    private final int maxViolations;
    private final String action;
    private long lastViolationTime = 0L;
    private long lastResetTime = System.currentTimeMillis();

    public SpeedA2() {
        FileConfiguration config = Wave.getInstance().getConfig();
        this.isEnabled = config.getBoolean("Checks.SpeedA2.ENABLED", true);
        this.maxValue = config.getDouble("Checks.SpeedA2.MAX-SPEED", 1.3882);
        this.maxViolations = config.getInt("Checks.SpeedA2.MAX-VIOLATIONS", 20);
        this.action = config.getString("Checks.SpeedA2.ACTION", "kick %player% \u00a7b\u00a7lWave \u00a77\u00bb \u00a7eUnfair Advantage.");
        this.violationsResetTime = config.getLong("violations-reset", 120000L);
    }

    @Override
    public void onPacket(User user, WrappedPacket wrappedPacket) {
        if (this.isEnabled) {
            Player player = user.getPlayer();
            UserData userData = Wave.getInstance().getUserData();
            if (player != null && (player.isOp() || player.getGameMode() == GameMode.CREATIVE || player.hasPermission("wave.bypass.speed"))) {
                return;
            }
            if (this.isHighLatency(user.getPlayer())) {
                return;
            }
            if (System.currentTimeMillis() - userData.getLastTeleportTime(player.getUniqueId()) < 4500L) {
                return;
            }
            if (wrappedPacket.isFlying()) {
                double deltaXZ = this.movementEngine.getDeltaXZ(wrappedPacket);
                if (deltaXZ > 190.0) {
                    return;
                }
                if (deltaXZ > this.maxValue) {
                    ++this.violations;
                    SetbackEngine.performSetback(user.getPlayer());
                    if (this.violations >= this.maxViolations && this.violations >= this.maxViolations) {
                        try {
                            String playerAction = this.action.replace("%player%", user.getName());
                            Wave.getInstance().getServer().getScheduler().runTask(Wave.getInstance(), () -> Wave.getInstance().getServer().dispatchCommand(Wave.getInstance().getServer().getConsoleSender(), playerAction));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    String debugInfo = String.valueOf(deltaXZ);
                    this.flag(user, "A2", "Very high BPS and rotation", this.violations, debugInfo);
                    if (player != null) {
                        CheckLogger.log(player.getName(), "SPEED", "Type: A2 Debug:" + debugInfo);
                    }
                    this.lastViolationTime = System.currentTimeMillis();
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

