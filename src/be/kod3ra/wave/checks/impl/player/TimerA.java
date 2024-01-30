package be.kod3ra.wave.checks.impl.player;

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
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

@CheckInfo(name = "TIMER")
public class TimerA
        extends Check {
    private final MovementEngine movementEngine;
    private final LocationEngine locationEngine = new LocationEngine();
    private final int maxPackets;
    private final String action;
    private final int maxViolations;
    private final int onJoinDisabledTime;
    private final long violationsResetTime;
    private final boolean isEnabled;
    private int packetCount = 0;
    private long lastResetTime = System.currentTimeMillis();

    public TimerA() {
        this.movementEngine = new MovementEngine();
        FileConfiguration config = Wave.getInstance().getConfig();
        this.isEnabled = config.getBoolean("Checks.TimerA.ENABLED", true);
        this.maxPackets = config.getInt("Checks.TimerA.MAX-PACKETS", 26);
        this.maxViolations = config.getInt("Checks.TimerA.MAX-VIOLATIONS", 60);
        this.action = config.getString("Checks.TimerA.ACTION", "kick %player% \u00a7b\u00a7lWave \u00a77\u00bb \u00a7eUnfair Advantage.");
        this.onJoinDisabledTime = config.getInt("Checks.TimerA.ON-JOIN-DISABLED-TIME", 7);
        this.violationsResetTime = config.getLong("violations-reset", 120000L);
        Bukkit.getScheduler().runTaskTimer(Wave.getInstance(), () -> {
            this.packetCount = 0;
        }, 0L, 20L);
    }

    @Override
    public void onPacket(User user, WrappedPacket wrappedPacket) {
        if (this.isEnabled && (wrappedPacket.isMovingAndRotation() || wrappedPacket.isRotation())) {
            Player player = user.getPlayer();
            UserData userData = Wave.getInstance().getUserData();
            Location currentLocation = this.locationEngine.getCurrentLocation(player);
            Location previousGroundLocation = this.locationEngine.getPreviousGroundLocation();
            if (user.getPlayer().getInventory().getItemInHand().getType().toString().contains("BOW")) {
                return;
            }
            if (player != null && (player.isOp() || player.getGameMode() == GameMode.CREATIVE || player.hasPermission("wave.bypass.timer"))) {
                return;
            }
            if (previousGroundLocation == null || currentLocation == null) {
                this.locationEngine.updatePreviousLocations(player);
                return;
            }
            long joinTime = userData.getJoinTime(player.getUniqueId());
            if (System.currentTimeMillis() - joinTime < (this.onJoinDisabledTime * 1000L)) {
                return;
            }
            ++this.packetCount;
            if (this.packetCount > this.maxPackets) {
                ++this.violations;
                SetbackEngine.performSetback(user.getPlayer());
                String debugInfo = "Packet count: " + this.packetCount;
                this.flag(user, "A", "Modified the time", this.violations, debugInfo);
                if (player != null) {
                    CheckLogger.log(player.getName(), "TIMER", "Type: A Debug:" + debugInfo);
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
            this.movementEngine.updateCoordinates(wrappedPacket);
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

