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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChecksGUI
        implements Listener {
    private static final Map<Player, ChecksGUI> playerInstances = new HashMap<Player, ChecksGUI>();
    private final Plugin plugin;
    private final FileConfiguration config;

    public ChecksGUI(Plugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public static ChecksGUI getInstance(Player player, Plugin plugin) {
        return playerInstances.computeIfAbsent(player, p -> new ChecksGUI(plugin));
    }

    public void openGUI(Player player) {
        ChecksGUI checksGUI = ChecksGUI.getInstance(player, this.plugin);
        Inventory gui = Bukkit.createInventory(player, 54, "\u00a7b\u00a7lWave \u00a7f\u00bb \u00a7eChecks GUI");
        for (int i = 0; i < gui.getSize(); ++i) {
            ItemStack glassPane;
            int row = i / 9;
            int col = i % 9;
            if (col >= 1 && col <= 7 && row >= 1 && row <= 4) continue;
            try {
                glassPane = new ItemStack(Material.valueOf("STAINED_GLASS_PANE"));
            } catch (IllegalArgumentException e) {
                glassPane = new ItemStack(Material.STAINED_GLASS_PANE);
            }
            ItemMeta glassPaneMeta = glassPane.getItemMeta();
            glassPaneMeta.setDisplayName(" ");
            glassPane.setItemMeta(glassPaneMeta);
            gui.setItem(i, glassPane);
        }
        ItemStack autoClickerAItem = this.createCustomItem("AutoClickerA");
        gui.setItem(10, autoClickerAItem);
        ItemStack autoClickerBItem = this.createCustomItem("AutoClickerB");
        gui.setItem(11, autoClickerBItem);
        ItemStack criticalsAItem = this.createCustomItem("CriticalsA");
        gui.setItem(12, criticalsAItem);
        ItemStack killAuraAItem = this.createCustomItem("KillAuraA");
        gui.setItem(13, killAuraAItem);
        ItemStack reachAItem = this.createCustomItem("ReachA");
        gui.setItem(14, reachAItem);
        ItemStack flightAItem = this.createCustomItem("FlightA");
        gui.setItem(15, flightAItem);
        ItemStack flightBItem = this.createCustomItem("FlightB");
        gui.setItem(16, flightBItem);
        ItemStack flightCItem = this.createCustomItem("FlightC");
        gui.setItem(19, flightCItem);
        ItemStack flightDItem = this.createCustomItem("FlightD");
        gui.setItem(20, flightDItem);
        ItemStack jesusAItem = this.createCustomItem("JesusA");
        gui.setItem(21, jesusAItem);
        ItemStack noFallAItem = this.createCustomItem("NoFallA");
        gui.setItem(22, noFallAItem);
        ItemStack speedAItem = this.createCustomItem("SpeedA");
        gui.setItem(23, speedAItem);
        ItemStack speedA2Item = this.createCustomItem("SpeedA2");
        gui.setItem(24, speedA2Item);
        ItemStack stepAItem = this.createCustomItem("StepA");
        gui.setItem(25, stepAItem);
        ItemStack badPacketsAItem = this.createCustomItem("BadPacketsA");
        gui.setItem(28, badPacketsAItem);
        ItemStack badPacketsBItem = this.createCustomItem("BadPacketsB");
        gui.setItem(29, badPacketsBItem);
        ItemStack badPacketsCItem = this.createCustomItem("BadPacketsC");
        gui.setItem(30, badPacketsCItem);
        ItemStack fastBowAItem = this.createCustomItem("FastBowA");
        gui.setItem(31, fastBowAItem);
        ItemStack timerAItem = this.createCustomItem("TimerA");
        gui.setItem(32, timerAItem);
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName("\u00a7cBack");
        backButton.setItemMeta(backMeta);
        gui.setItem(49, backButton);
        player.openInventory(gui);
    }

    private ItemStack createCustomItem(String check) {
        Material material = check.equalsIgnoreCase("AutoClickerA") || check.equalsIgnoreCase("AutoClickerB") ? Material.IRON_SWORD : (check.equalsIgnoreCase("KillAuraA") || check.equalsIgnoreCase("ReachA") || check.equalsIgnoreCase("CriticalsA") ? Material.DIAMOND_SWORD : (check.equalsIgnoreCase("FlightA") || check.equalsIgnoreCase("FlightB") || check.equalsIgnoreCase("FlightC") || check.equalsIgnoreCase("FlightD") ? Material.FEATHER : (check.equalsIgnoreCase("JesusA") ? Material.WATER_BUCKET : (check.equalsIgnoreCase("NoFallA") ? Material.DIAMOND_BOOTS : (check.equalsIgnoreCase("StepA") ? Material.IRON_BOOTS : (check.equalsIgnoreCase("SpeedA") || check.equalsIgnoreCase("SpeedA2") ? Material.SUGAR : (check.equalsIgnoreCase("BadPacketsA") || check.equalsIgnoreCase("BadPacketsB") || check.equalsIgnoreCase("BadPacketsC") || check.equalsIgnoreCase("BadPacketsC2") ? Material.FERMENTED_SPIDER_EYE : (check.equalsIgnoreCase("FastBowA") ? Material.BOW : (check.equalsIgnoreCase("TimerA") ? Material.COMPASS : Material.PAPER)))))))));
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        int maxViolations = this.config.getInt("Checks." + check + ".MAX-VIOLATIONS");
        boolean enabled = this.config.getBoolean("Checks." + check + ".ENABLED");
        String displayName = enabled ? "\u00a7a" + check : "\u00a7c" + check;
        meta.setDisplayName(displayName);
        meta.setLore(Arrays.asList("\u00a77ENABLED: " + (enabled ? "\u00a7aTRUE" : "\u00a7cFALSE"), "\u00a77Max Violations: \u00a7e" + maxViolations, "", "\u00a7eYou will have to restart/reload to apply this changes."));
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof Player) {
            Player player = (Player) event.getInventory().getHolder();
            if (event.getView().getTitle().equals("\u00a7b\u00a7lWave \u00a7f\u00bb \u00a7eChecks GUI")) {
                event.setCancelled(true);
                List<Integer> targetSlots = Arrays.asList(10, 11, 12, 13, 14, 15, 16, 19, 20, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 49);
                if (targetSlots.contains(event.getRawSlot())) {
                    ItemMeta meta;
                    ItemStack clickedItem;
                    String check = event.getCurrentItem().getItemMeta().getDisplayName().substring(2);
                    if (event.getClick().isLeftClick() || event.getClick().isRightClick()) {
                        boolean currentStatus = this.config.getBoolean("Checks." + check + ".ENABLED");
                        this.config.set("Checks." + check + ".ENABLED", !currentStatus ? 1 : 0);
                        this.plugin.saveConfig();
                        this.openGUI(player);
                    }
                    if ((clickedItem = event.getCurrentItem()).getType() == Material.BARRIER && (meta = clickedItem.getItemMeta()) != null && meta.getDisplayName().equals("\u00a7cBack")) {
                        MainGUI mainGUI = new MainGUI(this.plugin);
                        mainGUI.openGUI(player);
                    }
                }
            }
        }
    }
}

