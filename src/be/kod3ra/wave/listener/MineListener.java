/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.BlockBreakEvent
 */
package be.kod3ra.wave.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class MineListener
implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block brokenBlock;
        Material blockType;
        if (event.getPlayer() == null || (blockType = (brokenBlock = event.getBlock()).getType()) == Material.DIAMOND_ORE || blockType == Material.IRON_ORE || blockType == Material.GOLD_ORE || blockType == Material.COAL_ORE || blockType == Material.REDSTONE_ORE || blockType == Material.LAPIS_ORE) {
            // empty if block
        }
    }
}

