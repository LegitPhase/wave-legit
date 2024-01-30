/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
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
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "SPEED")
public final class SpeedA
        extends Check {
    private final MovementEngine movementEngine = new MovementEngine();
    private long ignoreTimeStart = 0L;
    private long lastResetTime = System.currentTimeMillis();
    private final boolean isEnabled;
    private final double maxValue;
    private final long violationsResetTime;
    private final int onJoinDisabledTime;
    private final int maxViolations;
    private final String action;

    public SpeedA() {
        FileConfiguration config = Wave.getInstance().getConfig();
        this.isEnabled = config.getBoolean("Checks.SpeedA.ENABLED", true);
        this.maxValue = config.getDouble("Checks.SpeedA.MAX-SPEED", 0.6141742081626725);
        this.maxViolations = config.getInt("Checks.SpeedA.MAX-VIOLATIONS", 20);
        this.onJoinDisabledTime = config.getInt("Checks.SpeedA.ON-JOIN-DISABLED-TIME", 15);
        this.action = config.getString("Checks.SpeedA.ACTION", "kick %player% \u00a7b\u00a7lWave \u00a77\u00bb \u00a7eUnfair Advantage.");
        this.violationsResetTime = config.getLong("violations-reset", 120000L);
    }

    @Override
    public void onPacket(User user, WrappedPacket wrappedPacket) {
        if (!this.isEnabled) {
            return;
        }
        Player player = user.getPlayer();
        UserData userData = Wave.getInstance().getUserData();
        if (player == null) {
            return;
        }
        if (System.currentTimeMillis() - userData.getLastDamageTime(player.getUniqueId()) < 1500L) {
            return;
        }
        long joinTime = userData.getJoinTime(player.getUniqueId());
        if (System.currentTimeMillis() - joinTime < (long) (this.onJoinDisabledTime * 1000L)) {
            return;
        }
        if (this.hasIceOrTrapdoorAround(player, 3) || this.isBlockAboveHeadSolid(player)) {
            this.ignoreTimeStart = System.currentTimeMillis() + 3000L;
            return;
        }
        if (wrappedPacket.isFlying() && !this.isHighLatency(player) && !this.isBypassed(player)) {
            double deltaXZ = this.movementEngine.getDeltaXZ(wrappedPacket);
            if (this.hasSpeedEffect(player)) {
                if (deltaXZ > 0.68 && deltaXZ <= 1.37) {
                    ++this.violations;
                    this.flag(user, "A", "High deltaXZ", this.violations, "DeltaXZ: " + deltaXZ);
                    this.ignoreTimeStart = System.currentTimeMillis();
                    if (this.violations >= this.maxViolations) {
                        this.handleViolation(user);
                    }
                }
            } else if (deltaXZ > this.maxValue && deltaXZ <= 1.37) {
                ++this.violations;
                this.flag(user, "A", "High deltaXZ", this.violations, "DeltaXZ: " + deltaXZ);
                if (player != null) {
                    CheckLogger.log(player.getName(), "SPEED", "Type: A Debug:" + deltaXZ);
                }
                this.ignoreTimeStart = System.currentTimeMillis();
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

    private boolean hasIceOrTrapdoorAround(Player player, int radius) {
        Location playerLocation = player.getLocation();
        int playerX = playerLocation.getBlockX();
        int playerY = playerLocation.getBlockY();
        int playerZ = playerLocation.getBlockZ();
        for (int x = playerX - radius; x <= playerX + radius; ++x) {
            for (int y = playerY - radius; y <= playerY + radius; ++y) {
                for (int z = playerZ - radius; z <= playerZ + radius; ++z) {
                    Block block = player.getWorld().getBlockAt(x, y, z);
                    String blockTypeName = block.getType().name();
                    if (!blockTypeName.contains("TRAPDOOR") && !blockTypeName.contains("TRAP_DOOR") && !blockTypeName.contains("ICE"))
                        continue;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasSpeedEffect(Player player) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (!effect.getType().equals(PotionEffectType.SPEED)) continue;
            int amplifier = effect.getAmplifier();
            if (amplifier == 1 || amplifier == 2) {
                return true;
            }
            if (amplifier < 3) continue;
            return false;
        }
        return false;
    }

    private boolean isBlockAboveHeadSolid(Player player) {
        Location playerLocation = player.getLocation();
        Block blockAboveHead = playerLocation.getBlock().getRelative(BlockFace.UP);
        return blockAboveHead.getType().isSolid();
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
        return player.isOp() || player.getGameMode() == GameMode.CREATIVE || player.hasPermission("wave.bypass.speed");
    }
}

