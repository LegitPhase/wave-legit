/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Player
 */
package be.kod3ra.wave.user.engine;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class GroundEngine {
    public static boolean isClientOnGround(Player player) {
        return player.isOnGround();
    }

    public static boolean isServerOnGround(Player player) {
        Block blockBelow1 = player.getLocation().getBlock().getRelative(0, -1, 0);
        Material materialBelow1 = blockBelow1.getType();
        Block blockBelow2 = player.getLocation().getBlock().getRelative(0, -2, 0);
        Material materialBelow2 = blockBelow2.getType();
        return !GroundEngine.isAir(materialBelow1) || !GroundEngine.isAir(materialBelow2) || GroundEngine.isSolid(materialBelow1) || GroundEngine.isSolid(materialBelow2);
    }

    private static boolean isAir(Material material) {
        return material == Material.AIR;
    }

    private static boolean isSolid(Material material) {
        return material.isSolid();
    }
}

