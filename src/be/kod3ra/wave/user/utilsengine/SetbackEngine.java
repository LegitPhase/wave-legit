/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 */
package be.kod3ra.wave.user.utilsengine;

import be.kod3ra.wave.Wave;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SetbackEngine {
    private static boolean enableSetback;

    public static void performSetback(Player player) {
        if (enableSetback) {
            Wave.getInstance().getServer().getScheduler().runTask((Plugin)Wave.getInstance(), () -> {
                player.setFlySpeed(0.0f);
                player.setWalkSpeed(0.0f);
                player.setFlying(false);
                player.getLocation().setPitch(-90.0f);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 15, 255, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 15, 0, false, false));
                Wave.getInstance().getServer().getScheduler().runTaskLater((Plugin)Wave.getInstance(), () -> {
                    player.setWalkSpeed(0.2f);
                    player.setFlySpeed(0.1f);
                }, 15L);
            });
        }
    }

    static {
        FileConfiguration config = Wave.getInstance().getConfig();
        enableSetback = config.getBoolean("setback", false);
    }
}

