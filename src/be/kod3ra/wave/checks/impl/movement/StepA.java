package be.kod3ra.wave.checks.impl.movement;

import be.kod3ra.wave.Wave;
import be.kod3ra.wave.checks.Check;
import be.kod3ra.wave.checks.impl.CheckInfo;
import be.kod3ra.wave.packet.WrappedPacket;
import be.kod3ra.wave.user.User;
import be.kod3ra.wave.user.UserData;
import be.kod3ra.wave.utils.CheckLogger;
import be.kod3ra.wave.utils.Latency;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@CheckInfo(name = "STEP")
public final class StepA
        extends Check {
    private final boolean isEnabled;
    private final double maxYSpeed;
    private final int maxViolations;
    private final String action;
    private final long violationsResetTime;
    private Location lastLocation;
    private long lastResetTime = System.currentTimeMillis();

    public StepA() {
        this.isEnabled = Wave.getInstance().getConfig().getBoolean("Checks.StepA.ENABLED", true);
        this.maxYSpeed = Wave.getInstance().getConfig().getDouble("Checks.StepA.MAX-Y-SPEED", 0.999);
        this.maxViolations = Wave.getInstance().getConfig().getInt("Checks.StepA.MAX-VIOLATIONS", 10);
        this.action = Wave.getInstance().getConfig().getString("Checks.StepA.ACTION", "kick %player% \u00a7b\u00a7lWave \u00a7f\u00bb \u00a7eUnfair Advantage");
        this.violationsResetTime = Wave.getInstance().getConfig().getLong("violations-reset", 120000L);
    }

    @Override
    public void onPacket(User user, WrappedPacket wrappedPacket) {
        if (!this.isEnabled) {
            return;
        }
        Player player = user.getPlayer();
        if (player != null && (player.isOp() || player.getGameMode() == GameMode.CREATIVE || player.hasPermission("wave.bypass.step"))) {
            return;
        }
        UserData userData = Wave.getInstance().getUserData();
        Location currentLocation = player.getLocation();
        if (this.isHighLatency(user.getPlayer())) {
            return;
        }
        if (this.lastLocation != null && wrappedPacket.isFlying()) {
            double deltaY = currentLocation.getY() - this.lastLocation.getY();
            if (deltaY > 1.0) {
                return;
            }
            if (deltaY > this.maxYSpeed && !player.isFlying()) {
                for (int x = -2; x <= 2; ++x) {
                    for (int y = -2; y <= 2; ++y) {
                        for (int z = -2; z <= 2; ++z) {
                            Location nearbyLocation = currentLocation.clone().add(x, y, z);
                            String blockName = nearbyLocation.getBlock().getType().name();
                            if (!blockName.contains("STAIRS")) continue;
                            return;
                        }
                    }
                }
                ++this.violations;
                String debugInfo = "DeltaY: " + deltaY;
                this.flag(user, "A", "Abnormal vertical player movement.", this.violations, debugInfo);
                if (player != null) {
                    CheckLogger.log(player.getName(), "STEP", "Type: A Debug:" + deltaY);
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
        this.lastLocation = currentLocation;
    }

    private boolean isHighLatency(Player player) {
        if (player == null) {
            return false;
        }
        int latency = Latency.getLag(player);
        return latency > 200;
    }
}

