package be.kod3ra.wave.commands.commands;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WaveKickCMD
        implements CommandExecutor {
    private final JavaPlugin plugin;
    private final Map<UUID, Long> commandCooldowns = new HashMap<UUID, Long>();

    public WaveKickCMD(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(this.plugin.getConfig().getString("wave-kick.usage"));
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(this.plugin.getConfig().getString("wave-kick.player-not-online"));
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
        this.applyEffects(target);
        this.sendKickMessage(target);
        this.showKickAnimation(target.getLocation());
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            target.kickPlayer(this.plugin.getConfig().getString("wave-kick.kick-message"));
            String confirmationMessage = this.plugin.getConfig().getString("wave-kick.confirmation").replace("%player%", target.getName());
            sender.sendMessage(confirmationMessage);
        }, 50L);
        return true;
    }

    private void showKickAnimation(Location location) {
        location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, 0);
        location.getWorld().playEffect(location, Effect.SMOKE, 0);
    }

    private void applyEffects(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 70, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 70, 10));
    }

    private void sendKickMessage(Player player) {
        String kickMessage = this.plugin.getConfig().getString("wave-animation.message-to-player");
        player.sendMessage("\u00a77\u00a7m---------------------------------");
        player.sendMessage("");
        player.sendMessage(kickMessage);
        player.sendMessage("");
        player.sendMessage("\u00a77\u00a7m---------------------------------");
    }
}

