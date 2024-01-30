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
import be.kod3ra.wave.user.engine.CPSEngine;
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
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@CheckInfo(name="AUTOCLICKER")
public final class AutoClickerB
extends Check {
    private CPSEngine cpsEngine;
    private boolean isEnabled;
    private int maxViolations;
    private long violationsResetTime;
    private int maxCps;
    private int Time;
    private String action;
    private long lastResetTime = System.currentTimeMillis();
    private final Map<UUID, Long> ignoredPlayers = new HashMap<UUID, Long>();
    private final long ignoreDuration = 4000L;

    public AutoClickerB() {
        FileConfiguration config = Wave.getInstance().getConfig();
        this.isEnabled = config.getBoolean("Checks.AutoClickerB.ENABLED", true);
        this.maxCps = config.getInt("Checks.AutoClickerB.MAX-CPS-IN-FIRST-CLICKING", 5);
        this.Time = config.getInt("Checks.AutoClickerB.TIME-FOR-MAX-CPS-IN-FIRST-CLICKING", 175);
        this.maxViolations = config.getInt("Checks.AutoClickerB.MAX-VIOLATIONS", 5);
        this.action = config.getString("Checks.AutoClickerB.ACTION", "kick %player% \u00a7b\u00a7lWave \u00a77\u00bb \u00a7eUnfair Advantage.");
        this.violationsResetTime = config.getLong("violations-reset", 120000L);
        this.cpsEngine = new CPSEngine(Wave.getInstance());
    }

    @Override
    public void onPacket(User user, WrappedPacket wrappedPacket) {
        UUID userUUID = user.getPlayer().getUniqueId();
        Player player = user.getPlayer();
        UserData userData = Wave.getInstance().getUserData();
        if (this.isEnabled && wrappedPacket.isAttacking()) {
            WrapperPlayClientInteractEntity wrapperPlayClientInteractEntity;
            WrapperPlayClientInteractEntity.InteractAction attackaction;
            if (this.shouldIgnorePlayer(userUUID)) {
                return;
            }
            if (player != null && (player.isOp() || player.getGameMode() == GameMode.CREATIVE || player.hasPermission("wave.bypass.autoclicker"))) {
                return;
            }
            if (wrappedPacket.isDigging() || this.isHighLatency(user.getPlayer())) {
                this.ignorePlayer(userUUID);
                return;
            }
            if (wrappedPacket.isAttacking() && !(attackaction = (wrapperPlayClientInteractEntity = new WrapperPlayClientInteractEntity(wrappedPacket.getPacketReceiveEvent())).getAction()).equals((Object)WrapperPlayClientInteractEntity.InteractAction.ATTACK)) {
                return;
            }
            long clickTime = System.currentTimeMillis();
            if (!this.cpsEngine.isClicking(userUUID)) {
                this.cpsEngine.startClick(userUUID);
            }
            this.cpsEngine.trackPlayerClick(userUUID, clickTime);
            int cps = this.cpsEngine.getCPS(userUUID, clickTime);
            String debugInfo = String.valueOf(cps);
            if (cps >= this.maxCps && clickTime - this.cpsEngine.getLastClickTime(userUUID) <= (long)this.Time) {
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
                this.flag(user, "B", "High CPS in a short time", this.violations, debugInfo);
                if (player != null) {
                    CheckLogger.log(player.getName(), "AUTOCLICKER", "Type: B Debug:" + debugInfo);
                }
            }
            if (System.currentTimeMillis() - this.lastResetTime > this.violationsResetTime) {
                this.violations = 0;
                this.lastResetTime = System.currentTimeMillis();
            }
            if (!wrappedPacket.isAttacking()) {
                this.cpsEngine.endClick(userUUID);
            }
        }
    }

    private boolean shouldIgnorePlayer(UUID userUUID) {
        return this.ignoredPlayers.containsKey(userUUID) && System.currentTimeMillis() < this.ignoredPlayers.get(userUUID);
    }

    private void ignorePlayer(UUID userUUID) {
        this.ignoredPlayers.put(userUUID, System.currentTimeMillis() + 4000L);
    }

    private boolean isHighLatency(Player player) {
        if (player == null) {
            return false;
        }
        int latency = Latency.getLag(player);
        return latency > 200;
    }
}
