/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.bukkit.GameMode
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
import be.kod3ra.wave.user.UserData;
import be.kod3ra.wave.user.engine.MovementEngine;
import be.kod3ra.wave.utils.CheckLogger;
import be.kod3ra.wave.utils.Latency;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@CheckInfo(name = "JESUS")
public final class JesusA
        extends Check {
    private final MovementEngine movementEngine = new MovementEngine();
    private long lastResetTime = System.currentTimeMillis();
    private final boolean isEnabled;
    private final double maxValue;
    private final long violationsResetTime;
    private final int maxViolations;
    private final String action;

    public JesusA() {
        FileConfiguration config = Wave.getInstance().getConfig();
        this.isEnabled = config.getBoolean("Checks.JesusA.ENABLED", true);
        this.maxValue = config.getDouble("Checks.JesusA.MAX-SPEED", 0.15);
        this.maxViolations = config.getInt("Checks.JesusA.MAX-VIOLATIONS", 20);
        this.action = config.getString("Checks.JesusA.ACTION", "kick %player% \u00a7b\u00a7lWave \u00a77\u00bb \u00a7eUnfair Advantage.");
        this.violationsResetTime = config.getLong("violations-reset", 120000L);
    }

    @Override
    public void onPacket(User user, WrappedPacket wrappedPacket) {
        Player player = user.getPlayer();
        UserData userData = Wave.getInstance().getUserData();
        if (!this.isEnabled || !wrappedPacket.isFlying() || this.isHighLatency(player) || this.isBypassed(player)) {
            return;
        }
        if (this.isHighLatency(user.getPlayer())) {
            return;
        }
        if (player.getFallDistance() > 0.0f) {
            return;
        }
        double deltaXZ = this.movementEngine.getDeltaXZ(wrappedPacket);
        if ((player.getLocation().getBlock().isLiquid() || player.getLocation().add(0.0, -1.0, 0.0).getBlock().isLiquid() || player.getLocation().add(0.0, -2.0, 0.0).getBlock().isLiquid()) && deltaXZ > this.maxValue) {
            ++this.violations;
            boolean ignoreCheck = false;
            for (int x = -4; x <= 4 && !ignoreCheck; ++x) {
                for (int y = -4; y <= 4 && !ignoreCheck; ++y) {
                    for (int z = -4; z <= 4 && !ignoreCheck; ++z) {
                        Block block = player.getLocation().add(x, y, z).getBlock();
                        String blockTypeName = block.getType().name();
                        if (!block.getType().isSolid() && !blockTypeName.contains("WATER_LILY") && !blockTypeName.contains("LILY_PAD"))
                            continue;
                        ignoreCheck = true;
                    }
                }
            }
            if (!ignoreCheck) {
                String debugInfo = String.valueOf(deltaXZ);
                this.flag(user, "A", "Anormal speed in/on water", this.violations, debugInfo);
                if (player != null) {
                    CheckLogger.log(player.getName(), "JESUS", "Type: A Debug:" + debugInfo);
                }
                if (this.violations >= this.maxViolations) {
                    this.handleViolation(user);
                }
            }
        }
        if (System.currentTimeMillis() - this.lastResetTime > this.violationsResetTime) {
            this.violations = 0;
            this.lastResetTime = System.currentTimeMillis();
        }
    }

    private void handleViolation(User user) {
        if (this.violations >= this.maxViolations) {
            try {
                String playerAction = this.action.replace("%player%", user.getName());
                Wave.getInstance().getServer().getScheduler().runTask(Wave.getInstance(), () -> Wave.getInstance().getServer().dispatchCommand(Wave.getInstance().getServer().getConsoleSender(), playerAction));
            } catch (Exception e) {
                e.printStackTrace();
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

    private boolean isBypassed(Player player) {
        return player.isOp() || player.getGameMode() == GameMode.CREATIVE || player.hasPermission("wave.bypass.jesus");
    }
}

