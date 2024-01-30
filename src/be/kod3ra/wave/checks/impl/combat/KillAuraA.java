/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.GameMode
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
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
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@CheckInfo(name="KILLAURA")
public final class KillAuraA
extends Check {
    private int usePackets;
    private int flyPackets;
    private int ratioThreshold;
    private long lastCheckTime;
    private long lastResetTime = System.currentTimeMillis();
    private boolean isEnabled;
    private long violationsResetTime;
    private String action;
    private int maxViolations;

    public KillAuraA() {
        FileConfiguration config = Wave.getInstance().getConfig();
        this.usePackets = 0;
        this.flyPackets = 0;
        this.lastCheckTime = System.currentTimeMillis();
        this.isEnabled = config.getBoolean("Checks.KillAuraA.ENABLED", true);
        this.maxViolations = config.getInt("Checks.KillAuraA.MAX-VIOLATIONS", 5);
        this.ratioThreshold = config.getInt("Checks.KillAuraA.RATIO-THRESHOLD", 1);
        this.action = config.getString("Checks.KillAuraA.ACTION", "wavekick %player%");
        this.violationsResetTime = config.getLong("violations-reset", 120000L);
    }

    @Override
    public void onPacket(User user, WrappedPacket wrappedPacket) {
        Player player = user.getPlayer();
        UserData userData = Wave.getInstance().getUserData();
        if (!this.isEnabled) {
            return;
        }
        if (player != null && (player.isOp() || player.getGameMode() == GameMode.CREATIVE || player.hasPermission("wave.bypass.killaura"))) {
            return;
        }
        if (this.isHighLatency(user.getPlayer())) {
            return;
        }
        if (wrappedPacket.isFlying()) {
            ++this.flyPackets;
        } else if (wrappedPacket.isAttacking()) {
            WrapperPlayClientInteractEntity wrapperPlayClientInteractEntity = new WrapperPlayClientInteractEntity(wrappedPacket.getPacketReceiveEvent());
            WrapperPlayClientInteractEntity.InteractAction attackAction = wrapperPlayClientInteractEntity.getAction();
            if (!attackAction.equals((Object)WrapperPlayClientInteractEntity.InteractAction.ATTACK)) {
                return;
            }
            ++this.usePackets;
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - this.lastCheckTime > 1000L) {
            if (this.flyPackets > 0 && this.usePackets > 0) {
                double flyUseRatio = (double)this.usePackets / (double)this.flyPackets;
                if (flyUseRatio > (double)this.ratioThreshold) {
                    ++this.violations;
                    String debugInfo = "FlyPackets: " + this.flyPackets + " UsePackets: " + this.usePackets + " FlyUseRatio: " + flyUseRatio;
                    this.flag(user, "A", "Use packets sent at the same time as flying packets", this.violations, debugInfo);
                    if (player != null) {
                        CheckLogger.log(player.getName(), "KILLAURA", "Type: A Debug:" + debugInfo);
                    }
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
                }
                if (System.currentTimeMillis() - this.lastResetTime > this.violationsResetTime) {
                    this.violations = 0;
                    this.lastResetTime = System.currentTimeMillis();
                }
            }
            this.usePackets = 0;
            this.flyPackets = 0;
            this.lastCheckTime = currentTime;
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

