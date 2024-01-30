/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 */
package be.kod3ra.wave.gui;

import be.kod3ra.wave.gui.MainGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class SettingsGUI
implements Listener {
    private final Plugin plugin;
    private final FileConfiguration config;

    public SettingsGUI(Plugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        Bukkit.getPluginManager().registerEvents((Listener)this, plugin);
    }

    public void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory((InventoryHolder)player, (int)27, (String)"\u00a7b\u00a7lWave \u00a7f\u00bb \u00a7eSettings GUI");
        for (int i = 0; i < gui.getSize(); ++i) {
            int row = i / 9;
            int col = i % 9;
            if (col != 0 && col != 8 && row != 0 && row != 2) continue;
            ItemStack glassPane = new ItemStack(Material.valueOf((String)"STAINED_GLASS_PANE"));
            ItemMeta glassPaneMeta = glassPane.getItemMeta();
            glassPaneMeta.setDisplayName(" ");
            glassPane.setItemMeta(glassPaneMeta);
            gui.setItem(i, glassPane);
        }
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName("\u00a7cBack");
        backButton.setItemMeta(backMeta);
        gui.setItem(22, backButton);
        boolean setbackValue = this.config.getBoolean("setback");
        ItemStack setbackItem = new ItemStack(Material.ARROW);
        ItemMeta setbackMeta = setbackItem.getItemMeta();
        String setbackDisplayName = setbackValue ? "\u00a7aSetback: TRUE" : "\u00a7cSetback: FALSE";
        setbackMeta.setDisplayName(setbackDisplayName);
        setbackItem.setItemMeta(setbackMeta);
        gui.setItem(10, setbackItem);
        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof Player) {
            Player player = (Player)event.getInventory().getHolder();
            if (event.getView().getTitle().equals("\u00a7b\u00a7lWave \u00a7f\u00bb \u00a7eSettings GUI")) {
                ItemMeta meta;
                ItemStack clickedItem;
                event.setCancelled(true);
                if ((event.getClick().isLeftClick() || event.getClick().isRightClick()) && (clickedItem = event.getCurrentItem()) != null && (meta = clickedItem.getItemMeta()) != null) {
                    if (meta.getDisplayName().equals("\u00a7cBack")) {
                        MainGUI mainGUI = new MainGUI(this.plugin);
                        mainGUI.openGUI(player);
                    } else if (event.getRawSlot() == 10) {
                        boolean currentStatus = this.config.getBoolean("setback");
                        this.config.set("setback", (Object)(!currentStatus ? 1 : 0));
                        this.plugin.saveConfig();
                        this.openGUI(player);
                    }
                }
            }
        }
    }
}

