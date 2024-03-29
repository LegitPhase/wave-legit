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
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@CheckInfo(name = "REACH")
public class ReachA
        extends Check {
    private static final long DETECTION_DELAY = 2000L;
    private final ReachEngine reachEngine = new ReachEngine();
    private final Map<UUID, Player> lastAttackedPositions = new HashMap<UUID, Player>();
    private final Map<UUID, Long> lastDetectionTimes = new HashMap<UUID, Long>();
    private final boolean isEnabled;
    private final double maxReachDistance;
    private final long violationsResetTime;
    private final int maxViolations;
    private final String action;
    private long lastResetTime = System.currentTimeMillis();

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
            if (wrappedPacket.isAttacking() && !(attackaction = (wrapperPlayClientInteractEntity = new WrapperPlayClientInteractEntity(wrappedPacket.getPacketReceiveEvent())).getAction()).equals(WrapperPlayClientInteractEntity.InteractAction.ATTACK)) {
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
                            Wave.getInstance().getServer().getScheduler().runTask(Wave.getInstance(), () -> Wave.getInstance().getServer().dispatchCommand(Wave.getInstance().getServer().getConsoleSender(), playerAction));
                        } catch (Exception e) {
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
        Wave.getInstance().getServer().getScheduler().runTask(Wave.getInstance(), () -> {
            for (Entity potentialTarget : attacker.getNearbyEntities(10.0, 10.0, 10.0)) {
                LivingEntity livingEntity;
                Vector targetDirection;
                if (potentialTarget.equals(attacker) || !(potentialTarget instanceof LivingEntity) || !((double) attackerDirection.angle(targetDirection = (livingEntity = (LivingEntity) potentialTarget).getEyeLocation().toVector().subtract(attacker.getEyeLocation().toVector())) < 0.3) || !(livingEntity instanceof Player))
                    continue;
                target[0] = (Player) livingEntity;
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

