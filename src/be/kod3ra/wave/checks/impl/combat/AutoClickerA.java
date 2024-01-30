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
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@CheckInfo(name = "AUTOCLICKER")
public final class AutoClickerA
        extends Check {
    private final Map<UUID, Long> ignoredPlayers = new HashMap<UUID, Long>();
    private final long ignoreDuration = 4000L;
    private final CPSEngine cpsEngine;
    private final boolean isEnabled;
    private final long violationsResetTime;
    private final int maxCps;
    private final int maxViolations;
    private final String action;
    private long lastResetTime = System.currentTimeMillis();

    public AutoClickerA() {
        FileConfiguration config = Wave.getInstance().getConfig();
        this.isEnabled = config.getBoolean("Checks.AutoClickerA.ENABLED", true);
        this.maxCps = config.getInt("Checks.AutoClickerA.MAX-CPS", 18);
        this.maxViolations = config.getInt("Checks.AutoClickerA.MAX-VIOLATIONS", 5);
        this.action = config.getString("Checks.AutoClickerA.ACTION", "kick %player% \u00a7b\u00a7lWave \u00a77\u00bb \u00a7eUnfair Advantage.");
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
            if (wrappedPacket.isAttacking() && !(attackaction = (wrapperPlayClientInteractEntity = new WrapperPlayClientInteractEntity(wrappedPacket.getPacketReceiveEvent())).getAction()).equals(WrapperPlayClientInteractEntity.InteractAction.ATTACK)) {
                return;
            }
            long clickTime = System.currentTimeMillis();
            this.cpsEngine.trackPlayerClick(userUUID, clickTime);
            int cps = this.cpsEngine.getCPS(userUUID, clickTime);
            String debugInfo = String.valueOf(cps);
            if (cps > this.maxCps) {
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
                this.flag(user, "A", "High CPS", this.violations, debugInfo);
                if (player != null) {
                    CheckLogger.log(player.getName(), "AUTOCLICKER", "Type: A Debug:" + debugInfo);
                }
            }
        }
        if (System.currentTimeMillis() - this.lastResetTime > this.violationsResetTime) {
            this.violations = 0;
            this.lastResetTime = System.currentTimeMillis();
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

