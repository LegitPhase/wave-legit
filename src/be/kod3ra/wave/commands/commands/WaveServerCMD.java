/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package be.kod3ra.wave.commands.commands;

import be.kod3ra.wave.Config;
import be.kod3ra.wave.utils.MemoryUtil;
import be.kod3ra.wave.utils.PingUtil;
import be.kod3ra.wave.utils.TPSUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WaveServerCMD
implements CommandExecutor {
    private final Config config;

    public WaveServerCMD(Config config) {
        this.config = config;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int averagePing;
        double[] tps = TPSUtil.getRecentTPS();
        int totalPing = 0;
        int playerCount = 0;
        for (Player onlinePlayer : sender.getServer().getOnlinePlayers()) {
            totalPing += PingUtil.getPing(onlinePlayer);
            ++playerCount;
        }
        int n = averagePing = playerCount > 0 ? totalPing / playerCount : 0;
        String lagStatus = averagePing < 50 && tps[0] > 19.5 ? this.config.getConfig().getString("wave-server.lagStatus.noLagging", "\u00a7aNo Lagging") : (averagePing >= 50 && averagePing <= 75 && tps[0] >= 18.5 && tps[0] <= 19.5 ? this.config.getConfig().getString("wave-server.lagStatus.aBitLagging", "\u00a7eA bit Lagging") : (averagePing > 75 && averagePing <= 200 && tps[0] >= 17.0 && tps[0] < 18.5 ? this.config.getConfig().getString("wave-server.lagStatus.lagging", "\u00a7cLagging") : (averagePing > 200 || averagePing > 0 && tps[0] < 17.0 ? this.config.getConfig().getString("wave-server.lagStatus.laggingMuch", "\u00a74Lagging Much") : this.config.getConfig().getString("wave-server.lagStatus.noLagging", "\u00a7aNo Lagging"))));
        long usedMemory = MemoryUtil.getUsedMemory();
        long maxMemory = MemoryUtil.getMaxMemory();
        sender.sendMessage("");
        sender.sendMessage(this.config.getConfig().getString("wave-server.header", "&b&lWave Server Information:"));
        sender.sendMessage("");
        sender.sendMessage(" " + this.config.getConfig().getString("wave-server.tps", "&7TPS (Ticks Per Second): &e%tps0%, %tps1%, %tps2%").replace("%tps0%", String.valueOf(tps[0])).replace("%tps1%", String.valueOf(tps[1])).replace("%tps2%", String.valueOf(tps[2])));
        sender.sendMessage(" " + this.config.getConfig().getString("wave-server.serverVersion", "&7Server Version: &e%serverVersion%").replace("%serverVersion%", sender.getServer().getVersion()));
        sender.sendMessage(" " + this.config.getConfig().getString("wave-server.playerCount", "&7Number of Connected Players: &e%playerCount%").replace("%playerCount%", String.valueOf(sender.getServer().getOnlinePlayers().size())));
        sender.sendMessage(" " + this.config.getConfig().getString("wave-server.averagePing", "&7Average Player Ping: &e%averagePing%").replace("%averagePing%", String.valueOf(averagePing)));
        sender.sendMessage(" " + this.config.getConfig().getString("wave-server.lagStatus", "&7Lag Status: %lagStatus%").replace("%lagStatus%", lagStatus));
        sender.sendMessage("");
        sender.sendMessage(" " + this.config.getConfig().getString("wave-server.processors", "&7Number of Available Processors: &e%processors%").replace("%processors%", String.valueOf(Runtime.getRuntime().availableProcessors())));
        sender.sendMessage(" " + this.config.getConfig().getString("wave-server.maxMemory", "&7Maximum Server Memory: &e%maxMemory% MB").replace("%maxMemory%", String.valueOf(maxMemory)));
        sender.sendMessage(" " + this.config.getConfig().getString("wave-server.usedMemory", "&7Used Server Memory: &e%usedMemory% MB").replace("%usedMemory%", String.valueOf(usedMemory)));
        sender.sendMessage("");
        sender.sendMessage(" " + this.config.getConfig().getString("wave-server.jreDirectory", "&7JRE Directory: &e%jreDirectory%").replace("%jreDirectory%", System.getProperty("java.home")));
        sender.sendMessage(" " + this.config.getConfig().getString("wave-server.jreVersion", "&7JRE Version: &e%jreVersion%").replace("%jreVersion%", System.getProperty("java.version")));
        sender.sendMessage("");
        sender.sendMessage(" " + this.config.getConfig().getString("wave-server.osArchitecture", "&7OS Architecture: &e%osArchitecture%").replace("%osArchitecture%", System.getProperty("os.arch")));
        sender.sendMessage(" " + this.config.getConfig().getString("wave-server.osName", "&7OS Name: &e%osName%").replace("%osName%", System.getProperty("os.name")));
        sender.sendMessage(" " + this.config.getConfig().getString("wave-server.osVersion", "&7OS Version: &e%osVersion%").replace("%osVersion%", System.getProperty("os.version")));
        sender.sendMessage("");
        return true;
    }
}

