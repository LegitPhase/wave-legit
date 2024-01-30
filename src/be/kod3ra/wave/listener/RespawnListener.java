/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerRespawnEvent
 */
package be.kod3ra.wave.listener;

import be.kod3ra.wave.Wave;
import be.kod3ra.wave.user.UserData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnListener
        implements Listener {
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        UserData userData = Wave.getInstance().getUserData();
        userData.setJoinTime(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }
}

