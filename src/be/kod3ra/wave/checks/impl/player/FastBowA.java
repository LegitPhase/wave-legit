/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.GameMode
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 */
package be.kod3ra.wave.checks.impl.player;

import be.kod3ra.wave.Wave;
import be.kod3ra.wave.checks.Check;
import be.kod3ra.wave.checks.impl.CheckInfo;
import be.kod3ra.wave.packet.WrappedPacket;
import be.kod3ra.wave.user.User;
import be.kod3ra.wave.user.engine.MovementEngine;
import be.kod3ra.wave.user.utilsengine.SetbackEngine;
import be.kod3ra.wave.utils.CheckLogger;
import be.kod3ra.wave.utils.Latency;
import java.util.Timer;
import java.util.TimerTask;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

@CheckInfo(name="FASTBOW")
public class FastBowA
extends Check {
    private MovementEngine movementEngine;
    private int packetCount = 0;
    private int maxPackets;
    private Timer timer;
    private long violationsResetTime;
    private String action;
    private int maxViolations;
    private long lastResetTime = System.currentTimeMillis();

    public FastBowA() {
        this.movementEngine = new MovementEngine();
        FileConfiguration config = Wave.getInstance().getConfig();
        this.maxPackets = config.getInt("Checks.FastBowA.MAX-PACKETS", 30);
        this.maxViolations = config.getInt("Checks.FastBowA.MAX-VIOLATIONS", 60);
        this.action = config.getString("Checks.FastBowA.ACTION", "kick %player% \u00a7b\u00a7lWave \u00a77\u00bb \u00a7eUnfair Advantage.");
        this.violationsResetTime = config.getLong("violations-reset", 120000L);
        this.timer = new Timer();
        this.timer.scheduleAtFixedRate(new TimerTask(){

            @Override
            public void run() {
                FastBowA.this.packetCount = 0;
            }
        }, 1000L, 1000L);
    }

    @Override
    public void onPacket(User user, WrappedPacket wrappedPacket) {
        ItemStack item;
        Player player = user.getPlayer();
        try {
            item = player.getInventory().getItemInHand();
        }
        catch (NoSuchMethodError e) {
            item = player.getItemInHand();
        }
        if (item != null && !item.getType().toString().contains("BOW")) {
            return;
        }
        if (player != null && (player.isOp() || player.getGameMode() == GameMode.CREATIVE || player.hasPermission("wave.bypass.fastbow"))) {
            return;
        }
        if (this.isHighLatency(user.getPlayer())) {
            return;
        }
        if (wrappedPacket.isMovingAndRotation() || wrappedPacket.isRotation()) {
            ++this.packetCount;
            if (this.packetCount > this.maxPackets) {
                ++this.violations;
                SetbackEngine.performSetback(user.getPlayer());
                String debugInfo = "Packet count: " + this.packetCount;
                this.flag(user, "A", "Modified bow shoot time", this.violations, debugInfo);
                if (player != null) {
                    CheckLogger.log(player.getName(), "FASTBOW", "Type: A Debug:" + debugInfo);
                }
                if (this.violations >= this.maxViolations && this.violations >= this.maxViolations) {
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

