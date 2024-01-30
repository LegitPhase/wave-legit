package be.kod3ra.wave.commands.commands;

import be.kod3ra.wave.Wave;
import be.kod3ra.wave.utils.ClientUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WaveClientCMD
        implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String usageMessage = this.getConfigMessage("wave-client.usage", "\u00a7b\u00a7lWave \u00a7f\u00bb \u00a7eUsage: /wave client <player>");
        String playerNotOnlineMessage = this.getConfigMessage("wave-client.player-not-online", "\u00a7b\u00a7lWave \u00a7f\u00bb \u00a7eThe specified player is not online.");
        String clientBrandMessage = this.getConfigMessage("wave-client.client-brand-message", "\u00a7b\u00a7lWave \u00a7f\u00bb \u00a7e%player% has joined the server using \u00a7b%client% \u00a7e/// \u00a7b%brand%");
        if (args.length != 1) {
            sender.sendMessage(usageMessage);
            return true;
        }
        Player targetPlayer = sender.getServer().getPlayer(args[0]);
        if (targetPlayer == null || !targetPlayer.isOnline()) {
            sender.sendMessage(playerNotOnlineMessage);
            return true;
        }
        String client = ClientUtil.getClient(targetPlayer);
        String brand = ClientUtil.getBrand(targetPlayer);
        String finalMessage = clientBrandMessage.replace("%player%", targetPlayer.getName()).replace("%client%", client).replace("%brand%", brand);
        sender.sendMessage(finalMessage);
        return true;
    }

    private String getConfigMessage(String path, String defaultValue) {
        return Wave.getInstance().getConfig().getString(path, defaultValue);
    }
}
