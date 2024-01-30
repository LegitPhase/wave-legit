package be.kod3ra.wave.commands.commands;

import be.kod3ra.wave.gui.MainGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class WaveGuiCMD
        implements CommandExecutor {
    private final Plugin plugin;

    public WaveGuiCMD(Plugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("\u00a7b\u00a7lWave \u00a7f\u00bb \u00a7eThis command is only for players!");
            return true;
        }
        Player player = (Player) sender;
        MainGUI mainGUI = new MainGUI(this.plugin);
        mainGUI.openGUI(player);
        return true;
    }
}

