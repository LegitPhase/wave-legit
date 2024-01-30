/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.bukkit.GameMode
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerChangedWorldEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.event.player.PlayerRespawnEvent
 *  org.bukkit.plugin.Plugin
 */
package be.kod3ra.wave.utils;

import be.kod3ra.wave.Wave;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class Latency
        implements Listener {
    private static Map<UUID, Map.Entry<Integer, Long>> packetTicks;
    private static Map<UUID, Long> lastPacket;
    private static Map<UUID, Integer> packets;
    private final List<UUID> blacklist;

    public Latency() {
        packetTicks = new HashMap<UUID, Map.Entry<Integer, Long>>();
        lastPacket = new HashMap<UUID, Long>();
        this.blacklist = new ArrayList<UUID>();
        packets = new HashMap<UUID, Integer>();
        Wave.getInstance().getServer().getPluginManager().registerEvents(this, Wave.getInstance());
    }

    public static Integer getLag(Player p) {
        return packets.getOrDefault(p.getUniqueId(), 0);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.blacklist.add(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        packetTicks.remove(uuid);
        lastPacket.remove(uuid);
        this.blacklist.remove(uuid);
        packets.remove(uuid);
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        this.blacklist.add(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        this.blacklist.add(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (!Wave.getInstance().isEnabled()) {
            return;
        }
        if (player.getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }
        int count = 0;
        long time = System.currentTimeMillis();
        if (packetTicks.containsKey(uuid)) {
            count = packetTicks.get(uuid).getKey();
            time = packetTicks.get(uuid).getValue();
        }
        if (lastPacket.containsKey(uuid)) {
            long ms = System.currentTimeMillis() - lastPacket.get(uuid);
            if (ms >= 100L) {
                this.blacklist.add(uuid);
            } else if (ms > 1L) {
                this.blacklist.remove(uuid);
            }
        }
        if (!this.blacklist.contains(uuid)) {
            ++count;
            if (packetTicks.containsKey(uuid) && TimeUtil.elapsed(time, 1000L)) {
                packets.put(uuid, count);
                count = 0;
                time = TimeUtil.nowlong();
            }
        }
        packetTicks.put(uuid, new AbstractMap.SimpleEntry<Integer, Long>(count, time));
        lastPacket.put(uuid, System.currentTimeMillis());
        if (count > 10) {
            Wave.getInstance().getLogger().info(player.getName() + " have a high latency. Count: " + count);
        }
    }
}

