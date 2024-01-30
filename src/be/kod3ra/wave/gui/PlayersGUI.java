package be.kod3ra.wave.gui;

import be.kod3ra.wave.utils.ClientUtil;
import be.kod3ra.wave.utils.PingUtil;
import be.kod3ra.wave.utils.TPSUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class PlayersGUI
        implements Listener {
    private final Plugin plugin;

    public PlayersGUI(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private ItemStack createPlayerSkullItem(Player player) {
        ItemStack skullItem = new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();
        skullMeta.setOwner(player.getName());
        skullMeta.setDisplayName("\u00a77" + player.getName());
        ArrayList<String> lore = new ArrayList<String>();
        lore.add("");
        lore.add("\u00a77PING: \u00a7e" + PingUtil.getPing(player));
        lore.add("\u00a77CLIENT: \u00a7e" + ClientUtil.getClient(player));
        lore.add("\u00a77BRAND: \u00a7e" + ClientUtil.getBrand(player));
        double[] tpsArray = TPSUtil.getRecentTPS();
        lore.add("\u00a77TPS: \u00a7e" + String.format("%.2f, %.2f, %.2f", tpsArray[0], tpsArray[1], tpsArray[2]));
        skullMeta.setLore(lore);
        skullItem.setItemMeta(skullMeta);
        return skullItem;
    }

    public void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory(player, 54, "\u00a7b\u00a7lWave \u00a7f\u00bb \u00a7ePlayers GUI");
        for (int i = 0; i < gui.getSize(); ++i) {
            int row = i / 9;
            int col = i % 9;
            if (col != 0 && col != 8 && row != 0 && row != 5) continue;
            ItemStack glassPane = new ItemStack(Material.valueOf("STAINED_GLASS_PANE"));
            ItemMeta glassPaneMeta = glassPane.getItemMeta();
            glassPaneMeta.setDisplayName(" ");
            glassPane.setItemMeta(glassPaneMeta);
            gui.setItem(i, glassPane);
        }
        int slot = 10;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            ItemStack playerHead = this.createPlayerSkullItem(onlinePlayer);
            gui.setItem(slot++, playerHead);
            if (slot != 48) continue;
            slot = 19;
        }
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName("\u00a7cBack");
        backButton.setItemMeta(backMeta);
        gui.setItem(49, backButton);
        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof Player) {
            Player player = (Player) event.getInventory().getHolder();
            if (event.getView().getTitle().equals("\u00a7b\u00a7lWave \u00a7f\u00bb \u00a7ePlayers GUI")) {
                ItemMeta meta;
                event.setCancelled(true);
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem != null && clickedItem.getType() == Material.BARRIER && (meta = clickedItem.getItemMeta()) != null && meta.getDisplayName().equals("\u00a7cBack")) {
                    MainGUI mainGUI = new MainGUI(this.plugin);
                    mainGUI.openGUI(player);
                }
            }
        }
    }
}

