/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.GameMode
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.util.Vector
 */
package be.kod3ra.wave.checks.impl.combat;

import be.kod3ra.wave.Wave;
import be.kod3ra.wave.checks.Check;
import be.kod3ra.wave.checks.impl.CheckInfo;
import be.kod3ra.wave.packet.WrappedPacket;
import be.kod3ra.wave.user.User;
import be.kod3ra.wave.user.engine.ReachEngine;
import be.kod3ra.wave.user.utilsengine.SetbackEngine;
import be.kod3ra.wave.utils.CheckLogger;
import be.kod3ra.wave.utils.Latency;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

@CheckInfo(name="REACH")
public class ReachA
extends Check {
    private ReachEngine reachEngine = new ReachEngine();
    private Map<UUID, Player> lastAttackedPositions = new HashMap<UUID, Player>();
    private Map<UUID, Long> lastDetectionTimes = new HashMap<UUID, Long>();
    private static final long DETECTION_DELAY = 2000L;
    private long lastResetTime = System.currentTimeMillis();
    private boolean isEnabled;
    private double maxReachDistance;
    private long violationsResetTime;
    private int maxViolations;
    private String action;

    public ReachA() {
        FileConfiguration config = Wave.getInstance().getConfig();
        this.isEnabled = config.getBoolean("Checks.ReachA.ENABLED", true);
        this.maxReachDistance = config.getDouble("Checks.ReachA.MAX-REACH-DISTANCE", 4.0);
        this.maxViolations = config.getInt("Checks.ReachA.MAX-VIOLATIONS", 20);
        this.action = config.getString("Checks.ReachA.ACTION", "kick %player% \u00a7b\u00a7lWave \u00a77\u00bb \u00a7eUnfair Advantage.");
        this.violationsResetTime = config.getLong("violations-reset", 120000L);
    }

    @Override
    public void onPacket(User user, WrappedPacket wrappedPacket) {
        Player player = user.getPlayer();
        if (this.isEnabled && wrappedPacket.isAttacking()) {
            WrapperPlayClientInteractEntity wrapperPlayClientInteractEntity;
            WrapperPlayClientInteractEntity.InteractAction attackaction;
            Player attacker = user.getPlayer();
            if (player != null && (player.isOp() || player.getGameMode() == GameMode.CREATIVE || player.hasPermission("wave.bypass.reach"))) {
                return;
            }
            if (wrappedPacket.isAttacking() && !(attackaction = (wrapperPlayClientInteractEntity = new WrapperPlayClientInteractEntity(wrappedPacket.getPacketReceiveEvent())).getAction()).equals((Object)WrapperPlayClientInteractEntity.InteractAction.ATTACK)) {
                return;
            }
            if (this.lastDetectionTimes.containsKey(attacker.getUniqueId())) {
                long lastDetectionTime = this.lastDetectionTimes.get(attacker.getUniqueId());
                long cooldownDuration = 2000L;
                if (System.currentTimeMillis() - lastDetectionTime < cooldownDuration) {
                    return;
                }
            }
            if (this.isHighLatency(user.getPlayer())) {
                return;
            }
            Player target = this.getTargetPlayer(attacker);
            if (target != null && (target.isOnGround() || target instanceof LivingEntity)) {
                this.lastAttackedPositions.put(target.getUniqueId(), target);
                double reachDistance = this.reachEngine.calculateReach(attacker, target);
                if (reachDistance > this.maxReachDistance && reachDistance <= 10.0) {
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
                    String debugInfo = String.valueOf(reachDistance);
                    this.flag(user, "A", "High Reach", this.violations, debugInfo);
                    if (player != null) {
                        CheckLogger.log(player.getName(), "REACH", "Type: A Debug:" + debugInfo);
                    }
                }
                this.lastDetectionTimes.put(attacker.getUniqueId(), System.currentTimeMillis());
            }
            if (System.currentTimeMillis() - this.lastResetTime > this.violationsResetTime) {
                this.violations = 0;
                this.lastResetTime = System.currentTimeMillis();
            }
        }
    }

    private Player getTargetPlayer(Player attacker) {
        return this.lastAttackedPositions.getOrDefault(attacker.getUniqueId(), this.findPotentialTarget(attacker));
    }

    private Player findPotentialTarget(Player attacker) {
        Vector attackerDirection = attacker.getEyeLocation().getDirection();
        Player[] target = new Player[]{null};
        Wave.getInstance().getServer().getScheduler().runTask((Plugin)Wave.getInstance(), () -> {
            for (Entity potentialTarget : attacker.getNearbyEntities(10.0, 10.0, 10.0)) {
                LivingEntity livingEntity;
                Vector targetDirection;
                if (potentialTarget.equals(attacker) || !(potentialTarget instanceof LivingEntity) || !((double)attackerDirection.angle(targetDirection = (livingEntity = (LivingEntity)potentialTarget).getEyeLocation().toVector().subtract(attacker.getEyeLocation().toVector())) < 0.3) || !(livingEntity instanceof Player)) continue;
                target[0] = (Player)livingEntity;
                break;
            }
        });
        return target[0];
    }

    private boolean isHighLatency(Player player) {
        if (player == null) {
            return false;
        }
        int latency = Latency.getLag(player);
        return latency > 200;
    }
}

