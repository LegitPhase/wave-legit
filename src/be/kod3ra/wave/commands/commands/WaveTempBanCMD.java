/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.BanList$Type
 *  org.bukkit.Bukkit
 *  org.bukkit.Effect
 *  org.bukkit.Location
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 */
package be.kod3ra.wave.commands.commands;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class WaveTempBanCMD
implements CommandExecutor {
    private final JavaPlugin plugin;
    private final Map<UUID, Long> commandCooldowns = new HashMap<UUID, Long>();

    public WaveTempBanCMD(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int banHours;
        if (args.length != 2) {
            sender.sendMessage(this.plugin.getConfig().getString("wave-tempban.usage"));
            return true;
        }
        Player target = Bukkit.getPlayer((String)args[0]);
        if (target == null) {
            sender.sendMessage(this.plugin.getConfig().getString("wave-tempban.player-not-online"));
            return true;
        }
        if (this.commandCooldowns.containsKey(target.getUniqueId())) {
            long cooldownTime = this.commandCooldowns.get(target.getUniqueId());
            long currentTime = System.currentTimeMillis();
            if (currentTime - cooldownTime < 3000L) {
                sender.sendMessage("Wait before execute again the command.");
                return true;
            }
        }
        this.commandCooldowns.put(target.getUniqueId(), System.currentTimeMillis());
        try {
            banHours = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException e) {
            sender.sendMessage(this.plugin.getConfig().getString("wave-tempban.invalid-hours"));
            return true;
        }
        GregorianCalendar calendar = new GregorianCalendar();
        ((Calendar)calendar).add(11, banHours);
        Date endDate = calendar.getTime();
        String formattedEndDate = this.formatDate(endDate);
        Bukkit.getBanList((BanList.Type)BanList.Type.NAME).addBan(target.getName(), this.plugin.getConfig().getString("wave-tempban.kick-message"), endDate, null);
        this.applyEffects(target);
        this.sendTempBanMessage(target, formattedEndDate);
        this.showBanAnimation(target.getLocation());
        Bukkit.getScheduler().runTaskLater((Plugin)this.plugin, () -> {
            target.kickPlayer(this.plugin.getConfig().getString("wave-tempban.ban-message"));
            String confirmationMessage = this.plugin.getConfig().getString("wave-tempban.confirmation").replace("%player%", target.getName());
            sender.sendMessage(confirmationMessage);
        }, 50L);
        return true;
    }

    private void showBanAnimation(Location location) {
        location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, 0);
        location.getWorld().playEffect(location, Effect.SMOKE, 0);
    }


    private void applyEffects(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 70, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 70, 10));
    }

    private void sendTempBanMessage(Player player, String formattedEndDate) {
        String tempBanMessage = this.plugin.getConfig().getString("wave-animation.message-to-player");
        player.sendMessage("\u00a77\u00a7m---------------------------------");
        player.sendMessage("");
        player.sendMessage(tempBanMessage.replace("%endtime%", formattedEndDate));
        player.sendMessage("");
        player.sendMessage("\u00a77\u00a7m---------------------------------");
    }

    private String formatDate(Date date) {
        return date.toString();
    }
}

