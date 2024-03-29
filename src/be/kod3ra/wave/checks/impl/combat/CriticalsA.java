package be.kod3ra.wave.checks.impl.combat;

import be.kod3ra.wave.Wave;
import be.kod3ra.wave.checks.Check;
import be.kod3ra.wave.checks.impl.CheckInfo;
import be.kod3ra.wave.packet.WrappedPacket;
import be.kod3ra.wave.user.User;
import be.kod3ra.wave.user.UserData;
import be.kod3ra.wave.user.utilsengine.SetbackEngine;
import be.kod3ra.wave.utils.CheckLogger;
import be.kod3ra.wave.utils.Latency;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

@CheckInfo(name = "CRITICALS")
public final class CriticalsA
        extends Check {
    private final boolean isEnabled;
    private final int maxViolations;
    private final int timeDifference;
    private final long violationsResetTime;
    private final String action;
    private final long attackToleranceWindow = 3000L;
    private long lastFlyingTime;
    private long lastResetTime = System.currentTimeMillis();
    private long lastAttackTime = 0L;

    public CriticalsA() {
        FileConfiguration config = Wave.getInstance().getConfig();
        this.isEnabled = config.getBoolean("Checks.CriticalsA.ENABLED", true);
        this.timeDifference = config.getInt("Checks.CriticalsA.TIME-DIFFERENCE", 1);
        this.maxViolations = config.getInt("Checks.CriticalsA.MAX-VIOLATIONS", 15);
        this.action = config.getString("Checks.CriticalsA.ACTION", "kick %player% \u00a7b\u00a7lWave \u00a77\u00bb \u00a7eUnfair Advantage.");
        this.violationsResetTime = config.getLong("violations-reset", 120000L);
    }

    @Override
    public void onPacket(User user, WrappedPacket wrappedPacket) {
        long timeDifference;
        Player player = user.getPlayer();
        UserData userData = Wave.getInstance().getUserData();
        if (!this.isEnabled) {
            return;
        }
        if (player != null && (player.isOp() || player.getGameMode() == GameMode.CREATIVE || player.hasPermission("wave.bypass.criticals"))) {
            return;
        }
        if (this.isHighLatency(user.getPlayer())) {
            return;
        }
        if (wrappedPacket.isAttacking()) {
            WrapperPlayClientInteractEntity wrapperPlayClientInteractEntity = new WrapperPlayClientInteractEntity(wrappedPacket.getPacketReceiveEvent());
            WrapperPlayClientInteractEntity.InteractAction attackaction = wrapperPlayClientInteractEntity.getAction();
            if (attackaction.equals(WrapperPlayClientInteractEntity.InteractAction.INTERACT) || attackaction.equals(WrapperPlayClientInteractEntity.InteractAction.INTERACT_AT)) {
                if (System.currentTimeMillis() - this.lastAttackTime < this.attackToleranceWindow) {
                    return;
                }
                if (!attackaction.equals(WrapperPlayClientInteractEntity.InteractAction.ATTACK)) {
                    return;
                }
            }
            if (attackaction.equals(WrapperPlayClientInteractEntity.InteractAction.ATTACK)) {
                this.lastAttackTime = System.currentTimeMillis();
            }
        }
        if (wrappedPacket.isFlying()) {
            this.lastFlyingTime = System.currentTimeMillis();
        } else if (wrappedPacket.isAttacking() && (timeDifference = System.currentTimeMillis() - this.lastFlyingTime) < (long) this.timeDifference) {
            ++this.violations;
            String debugInfo = String.valueOf(timeDifference);
            this.flag(user, "A", "Very fast flying packets when attacking", this.violations, debugInfo);
            if (player != null) {
                CheckLogger.log(player.getName(), "CRITICALS", "Type: A Debug:" + debugInfo);
            }
            SetbackEngine.performSetback(user.getPlayer());
            if (this.violations >= this.maxViolations) {
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
    }

    private boolean isHighLatency(Player player) {
        if (player == null) {
            return false;
        }
        int latency = Latency.getLag(player);
        return latency > 200;
    }
}

