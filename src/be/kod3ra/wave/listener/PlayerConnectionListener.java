/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 */
package be.kod3ra.wave.listener;

import be.kod3ra.wave.Wave;
import be.kod3ra.wave.user.UserData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public final class PlayerConnectionListener
        implements Listener {
    @EventHandler
    private void onJoin(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
        UUID uuid = player.getUniqueId();
        UserData userData = Wave.getInstance().getUserData();
        userData.createUserData(player);
        userData.setJoinTime(uuid, System.currentTimeMillis());
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent playerQuitEvent) {
        Player player = playerQuitEvent.getPlayer();
        UUID uuid = player.getUniqueId();
        UserData userData = Wave.getInstance().getUserData();
        userData.deleteUserData(player);
    }
}

