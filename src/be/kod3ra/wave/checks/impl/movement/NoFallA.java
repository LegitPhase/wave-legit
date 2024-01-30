package be.kod3ra.wave.checks.impl.movement;

import be.kod3ra.wave.Wave;
import be.kod3ra.wave.checks.Check;
import be.kod3ra.wave.checks.impl.CheckInfo;
import be.kod3ra.wave.packet.WrappedPacket;
import be.kod3ra.wave.user.User;
import be.kod3ra.wave.user.UserData;
import be.kod3ra.wave.user.engine.GroundEngine;
import be.kod3ra.wave.utils.CheckLogger;
import be.kod3ra.wave.utils.Latency;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

@CheckInfo(name = "NOFALL")
public final class NoFallA
        extends Check {
    private final boolean enabled;
    private final int maxViolations;
    private final String kickAction;
    private final long violationsResetTime;
    private long lastResetTime = System.currentTimeMillis();

    public NoFallA() {
        FileConfiguration config = Wave.getInstance().getConfig();
        this.enabled = config.getBoolean("Checks.NoFallA.ENABLED", true);
        this.maxViolations = config.getInt("Checks.NoFallA.MAX-VIOLATIONS", 45);
        this.kickAction = config.getString("Checks.NoFallA.ACTION", "kick %player% \u00a7b\u00a7lWave \u00a7f\u00bb \u00a7eUnfair Advantage");
        this.violationsResetTime = config.getLong("violations-reset", 120000L);
    }

    @Override
    public void onPacket(User user, WrappedPacket wrappedPacket) {
        Player player = user.getPlayer();
        UserData userData = Wave.getInstance().getUserData();
        if (!this.enabled) {
            return;
        }
        if (player != null && (player.isOp() || player.getGameMode() == GameMode.CREATIVE || player.hasPermission("wave.bypass.nofall"))) {
            return;
        }
        if (this.isHighLatency(user.getPlayer())) {
            return;
        }
        if (this.hasSolidBlockNearby(user.getPlayer().getLocation())) {
            return;
        }
        boolean isClientOnGround = GroundEngine.isClientOnGround(user.getPlayer());
        boolean isServerOnGround = GroundEngine.isServerOnGround(user.getPlayer());
        if (isClientOnGround && !isServerOnGround) {
            ++this.violations;
            String debugInfo = "Client Ground: " + isClientOnGround + " | Server Ground: " + isServerOnGround;
            this.flag(user, "A", "Client on ground, but not on server", this.violations, debugInfo);
            if (player != null) {
                CheckLogger.log(player.getName(), "NOFALL", "Type: A Debug:" + debugInfo);
            }
            if (this.violations >= this.maxViolations) {
                try {
                    String playerAction = this.kickAction.replace("%player%", user.getName());
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

    public int getMaxViolations() {
        return this.maxViolations;
    }

    public String getKickAction() {
        return this.kickAction;
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

