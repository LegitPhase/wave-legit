/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
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

import be.kod3ra.wave.gui.ChecksGUI;
import be.kod3ra.wave.gui.PlayersGUI;
import be.kod3ra.wave.gui.SettingsGUI;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class MainGUI
implements Listener {
    private final Plugin plugin;
    private static final Map<Player, MainGUI> playerInstances = new HashMap<Player, MainGUI>();

    public MainGUI(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents((Listener)this, plugin);
    }

    public static MainGUI getInstance(Player player, Plugin plugin) {
        return playerInstances.computeIfAbsent(player, p -> new MainGUI(plugin));
    }

    public void openGUI(Player player) {
        MainGUI mainGUI = MainGUI.getInstance(player, this.plugin);
        Inventory gui = Bukkit.createInventory((InventoryHolder)player, (int)27, (String)"\u00a7b\u00a7lWave \u00a7f\u00bb \u00a7eMain GUI");
        ItemStack cpsSword = new ItemStack(Material.COMPASS);
        ItemMeta cpsSwordMeta = cpsSword.getItemMeta();
        cpsSwordMeta.setDisplayName("\u00a77Players");
        cpsSword.setItemMeta(cpsSwordMeta);
        gui.setItem(10, cpsSword);
        ItemStack checksBook = new ItemStack(Material.BOOK);
        ItemMeta checksBookMeta = checksBook.getItemMeta();
        checksBookMeta.setDisplayName("\u00a77Checks");
        checksBook.setItemMeta(checksBookMeta);
        gui.setItem(13, checksBook);
        ItemStack settingsAnvil = new ItemStack(Material.ANVIL);
        ItemMeta settingsAnvilMeta = settingsAnvil.getItemMeta();
        settingsAnvilMeta.setDisplayName("\u00a77Settings");
        settingsAnvil.setItemMeta(settingsAnvilMeta);
        gui.setItem(16, settingsAnvil);
        for (int i = 0; i < gui.getSize(); ++i) {
            if (gui.getItem(i) != null) continue;
            try {
                ItemStack glassPane = new ItemStack(Material.valueOf((String)"STAINED_GLASS_PANE"));
                ItemMeta glassPaneMeta = glassPane.getItemMeta();
                glassPaneMeta.setDisplayName(" ");
                glassPane.setItemMeta(glassPaneMeta);
                gui.setItem(i, glassPane);
                continue;
            }
            catch (IllegalArgumentException e) {
                ItemStack glassPane = new ItemStack(Material.valueOf((String)"LEGACY_STAINED_GLASS_PANE"));
                ItemMeta glassPaneMeta = glassPane.getItemMeta();
                glassPaneMeta.setDisplayName(" ");
                glassPane.setItemMeta(glassPaneMeta);
                gui.setItem(i, glassPane);
            }
        }
        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof Player) {
            Player player = (Player)event.getInventory().getHolder();
            if (event.getView().getTitle().equals("\u00a7b\u00a7lWave \u00a7f\u00bb \u00a7eMain GUI")) {
                event.setCancelled(true);
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                    if (clickedItem.getItemMeta().getDisplayName().equals("\u00a77Checks")) {
                        ChecksGUI checksGUI = new ChecksGUI(this.plugin);
                        checksGUI.openGUI(player);
                    }
                    if (clickedItem.getItemMeta().getDisplayName().equals("\u00a77Settings")) {
                        SettingsGUI settingsGUI = new SettingsGUI(this.plugin);
                        settingsGUI.openGUI(player);
                    }
                    if (clickedItem.getItemMeta().getDisplayName().equals("\u00a77Players")) {
                        PlayersGUI playersGUI = new PlayersGUI(this.plugin);
                        playersGUI.openGUI(player);
                    }
                }
            }
        }
    }
}

