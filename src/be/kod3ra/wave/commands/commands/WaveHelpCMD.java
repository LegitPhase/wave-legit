/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 */
package be.kod3ra.wave.commands.commands;

import be.kod3ra.wave.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class WaveHelpCMD
implements CommandExecutor {
    private final Config config;

    public WaveHelpCMD(Config config) {
        this.config = config;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("");
        sender.sendMessage("");
        sender.sendMessage(this.config.getConfig().getString("wave-help.header"));
        if (this.config.getConfig().isConfigurationSection("wave-help.commands")) {
            for (String key : this.config.getConfig().getConfigurationSection("wave-help.commands").getKeys(false)) {
                String message = this.config.getConfig().getString("wave-help.commands." + key);
                sender.sendMessage(" " + message);
            }
        }
        sender.sendMessage("");
        return true;
    }
}

