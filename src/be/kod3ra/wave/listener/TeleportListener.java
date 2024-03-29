package be.kod3ra.wave.listener;

import be.kod3ra.wave.Wave;
import be.kod3ra.wave.user.UserData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportListener
        implements Listener {
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        UserData userData = Wave.getInstance().getUserData();
        userData.setLastTeleportTime(player.getUniqueId(), System.currentTimeMillis());
    }
}

