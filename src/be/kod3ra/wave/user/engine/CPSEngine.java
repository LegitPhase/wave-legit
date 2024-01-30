/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package be.kod3ra.wave.user.engine;

import be.kod3ra.wave.Wave;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class CPSEngine
        implements Listener {
    private final Map<UUID, Long> clickStartTimes = new HashMap<UUID, Long>();
    private final Map<UUID, Integer> clickCounters = new HashMap<UUID, Integer>();

    public CPSEngine(Wave plugin) {
        new BukkitRunnable() {

            public void run() {
                CPSEngine.this.resetClickCounters();
            }
        }.runTaskTimer(plugin, 20L, 20L);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void startClick(UUID playerId) {
        this.clickStartTimes.put(playerId, System.currentTimeMillis());
    }

    public void endClick(UUID playerId) {
        this.clickStartTimes.remove(playerId);
        this.clickCounters.put(playerId, 0);
    }

    public void trackPlayerClick(UUID playerId, long clickTime) {
        this.clickCounters.put(playerId, this.clickCounters.getOrDefault(playerId, 0) + 1);
    }

    public int getCPS(UUID playerId, long clickTime) {
        return this.clickCounters.getOrDefault(playerId, 0);
    }

    private void resetClickCounters() {
        for (Player player : this.getOnlinePlayers()) {
            UUID playerId = player.getUniqueId();
            this.clickCounters.put(playerId, 0);
        }
    }

    public long getLastClickTime(UUID playerId) {
        return this.clickStartTimes.getOrDefault(playerId, 0L);
    }

    public boolean isClicking(UUID playerId) {
        return this.clickStartTimes.containsKey(playerId);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.resetClickCounters();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.resetClickCounters();
    }

    public List<Player> getOnlinePlayers() {
        ArrayList<Player> onlinePlayers = new ArrayList<Player>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            onlinePlayers.add(player);
        }
        return onlinePlayers;
    }
}

