/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.plugin.java.JavaPlugin
 */
package be.kod3ra.wave;

import be.kod3ra.wave.Config;
import be.kod3ra.wave.commands.Macro;
import be.kod3ra.wave.commands.commands.WaveBanCMD;
import be.kod3ra.wave.commands.commands.WaveCMD;
import be.kod3ra.wave.commands.commands.WaveClientCMD;
import be.kod3ra.wave.commands.commands.WaveGuiCMD;
import be.kod3ra.wave.commands.commands.WaveHelpCMD;
import be.kod3ra.wave.commands.commands.WaveKickCMD;
import be.kod3ra.wave.commands.commands.WaveNotifyCMD;
import be.kod3ra.wave.commands.commands.WaveServerCMD;
import be.kod3ra.wave.commands.commands.WaveTempBanCMD;
import be.kod3ra.wave.listener.DamageListener;
import be.kod3ra.wave.listener.MineListener;
import be.kod3ra.wave.listener.PlayerConnectionListener;
import be.kod3ra.wave.listener.RespawnListener;
import be.kod3ra.wave.listener.TeleportListener;
import be.kod3ra.wave.user.UserData;
import be.kod3ra.wave.user.UserPacketListener;
import be.kod3ra.wave.utils.HighPing;
import be.kod3ra.wave.utils.Latency;
import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.bstats.Metrics;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Wave
extends JavaPlugin {
    private static Wave instance;
    private final UserData userData = new UserData();
    public static final String ANSI_RESET = "\u001b[0m";
    public static final String ANSI_YELLOW = "\u001b[33m";
    public static final String ANSI_BOLD = "\u001b[1m";

    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build((Plugin)this));
        PacketEvents.getAPI().getSettings().checkForUpdates(false).bStats(false);
        PacketEvents.getAPI().load();
    }

    public void onEnable() {
        instance = this;
        Config.getInstance().load();
        PacketEvents.getAPI().getEventManager().registerListener(new UserPacketListener());
        PacketEvents.getAPI().init();
        this.getServer().getPluginManager().registerEvents((Listener)new TeleportListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new RespawnListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new DamageListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new MineListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new Macro(), (Plugin)this);
        new Latency();
        HighPing pingChecker = new HighPing();
        pingChecker.startChecking();
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents((Listener)new PlayerConnectionListener(), (Plugin)Wave.getInstance());
        this.getCommand("wave").setExecutor((CommandExecutor)new WaveCMD());
        this.getCommand("wavehelp").setExecutor((CommandExecutor)new WaveHelpCMD(Config.getInstance()));
        this.getCommand("wavegui").setExecutor((CommandExecutor)new WaveGuiCMD((Plugin)this));
        this.getCommand("waveclient").setExecutor((CommandExecutor)new WaveClientCMD());
        this.getCommand("wavenotify").setExecutor((CommandExecutor)new WaveNotifyCMD(this));
        this.getCommand("waveserver").setExecutor((CommandExecutor)new WaveServerCMD(Config.getInstance()));
        this.getCommand("wavekick").setExecutor((CommandExecutor)new WaveKickCMD(this));
        this.getCommand("waveban").setExecutor((CommandExecutor)new WaveBanCMD(this));
        this.getCommand("wavetempban").setExecutor((CommandExecutor)new WaveTempBanCMD(this));
        this.saveDefaultConfig();
        this.getLogger().info("\u001b[33m  __          __\u001b[0m");
        this.getLogger().info("\u001b[33m  \\ \\        / /\u001b[0m");
        this.getLogger().info("\u001b[33m   \\ \\  /\\  / /_ ___   _____\u001b[0m");
        this.getLogger().info("\u001b[33m    \\ \\/  \\/ / _` \\ \\ / / _ \\\u001b[0m");
        this.getLogger().info("\u001b[33m     \\  /\\  / (_| |\\ V /  __/\u001b[0m");
        this.getLogger().info("\u001b[33m      \\/  \\/ \\__,_| \\_/ \\___|\u001b[0m");
        this.getLogger().info("");
        this.getLogger().info("\u001b[33m\u001b[1m +---------------------------+\u001b[0m");
        this.getLogger().info("\u001b[33m\u001b[1m | Authors: Kod3ra, bedike16 |\u001b[0m");
        this.getLogger().info("\u001b[33m\u001b[1m | Version: BETA 0.1.0       |\u001b[0m");
        this.getLogger().info("\u001b[33m\u001b[1m +---------------------------+\u001b[0m");
        this.getLogger().info("");
        int pluginId = 20787;
        Metrics metrics = new Metrics(this, pluginId);
    }

    public void onDisable() {
        PacketEvents.getAPI().terminate();
    }

    public static Wave getInstance() {
        return instance;
    }

    public UserData getUserData() {
        return this.userData;
    }
}

