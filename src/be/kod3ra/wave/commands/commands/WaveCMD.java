/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.file.FileConfiguration
 */
package be.kod3ra.wave.commands.commands;

import be.kod3ra.wave.Wave;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class WaveCMD
        implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration config = Wave.getInstance().getConfig();
        String message = config.getString("wave-command", "\u00a7b\u00a7lWave \u00a7eBETA 0.1.0 \u00a7fis running on the server!");
        sender.sendMessage(message);
        return true;
    }
}

