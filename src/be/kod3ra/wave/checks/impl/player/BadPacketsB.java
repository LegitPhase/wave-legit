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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

@CheckInfo(name = "BADPACKETS")
public final class BadPacketsB
        extends Check {
    private final boolean isEnabled;
    private final int maxPitch;
    private final int maxViolations;
    private final String action;
    private final long violationsResetTime;
    private long lastResetTime = System.currentTimeMillis();

    public BadPacketsB() {
        FileConfiguration config = Wave.getInstance().getConfig();
        this.isEnabled = config.getBoolean("Checks.BadPacketsB.ENABLED");
        this.maxPitch = config.getInt("Checks.BadPacketsB.MAX-PITCH");
        this.maxViolations = config.getInt("Checks.BadPacketsB.MAX-VIOLATIONS");
        this.action = config.getString("Checks.BadPacketsB.ACTION");
        this.violationsResetTime = config.getLong("violations-reset", 120000L);
    }

    @Override
    public void onPacket(User user, WrappedPacket wrappedPacket) {
        if (this.isEnabled && (wrappedPacket.isRotation() || wrappedPacket.isMovingAndRotation())) {
            Player player = user.getPlayer();
            UserData userData = Wave.getInstance().getUserData();
            float playerPitch = player.getLocation().getPitch();
            if (this.isHighLatency(user.getPlayer())) {
                return;
            }
            if (player != null && (player.isOp() || player.getGameMode() == GameMode.CREATIVE || player.hasPermission("wave.bypass.badpackets"))) {
                return;
            }
            if (playerPitch > (float) this.maxPitch) {
                ++this.violations;
                SetbackEngine.performSetback(user.getPlayer());
                String debugInfo = "Pitch: " + playerPitch;
                this.flag(user, "B", "Invalid Pitch", this.violations, debugInfo);
                if (player != null) {
                    CheckLogger.log(player.getName(), "BADPACKETS", "Type: B Debug:" + debugInfo);
                }
                if (this.violations >= this.maxViolations) {
                    try {
                        String playerAction = this.action.replace("%player%", user.getName());
                        Wave.getInstance().getServer().getScheduler().runTask(Wave.getInstance(), () -> Wave.getInstance().getServer().dispatchCommand(Wave.getInstance().getServer().getConsoleSender(), playerAction));
                    } catch (Exception e) {
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

