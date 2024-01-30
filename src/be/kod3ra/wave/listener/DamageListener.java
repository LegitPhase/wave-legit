/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.GameMode
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityDamageEvent
 */
package be.kod3ra.wave.listener;

import be.kod3ra.wave.Wave;
import be.kod3ra.wave.user.UserData;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener
implements Listener {
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player)event.getEntity();
            UserData userData = Wave.getInstance().getUserData();
            if (!player.isOp() && player.getGameMode() != GameMode.CREATIVE && !player.hasPermission("wave.bypass")) {
                userData.setLastDamageTime(player.getUniqueId(), System.currentTimeMillis());
                userData.setLastAttackTime(player.getUniqueId(), System.currentTimeMillis());
                userData.setLastDamageIgnoredTime(player.getUniqueId(), System.currentTimeMillis());
            }
        }
    }
}

