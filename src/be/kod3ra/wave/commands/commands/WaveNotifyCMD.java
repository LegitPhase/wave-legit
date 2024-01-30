package be.kod3ra.wave.commands.commands;

import be.kod3ra.wave.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class WaveNotifyCMD
        implements CommandExecutor {
    private final JavaPlugin plugin;

    public WaveNotifyCMD(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(this.plugin.getConfig().getString("wave-notify.usage"));
            return true;
        }
        String target = args[0].toLowerCase();
        String message = ColorUtil.format(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
        String prefix = ColorUtil.format(this.plugin.getConfig().getString("wave-notify.prefix"));
        if (target.equals("everyone")) {
            this.broadcastMessage(prefix + message);
        } else if (target.equals("staff")) {
            this.sendToStaff(prefix + message);
        } else {
            sender.sendMessage(prefix + this.plugin.getConfig().getString("wave-notify.invalid-target"));
        }
        return true;
    }

    private void broadcastMessage(String message) {
        Bukkit.broadcastMessage(message);
    }

    private void sendToStaff(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission("wave.notify")) continue;
            player.sendMessage(message);
        }
    }
}

