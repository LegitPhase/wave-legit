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

import be.kod3ra.wave.commands.Macro;
import be.kod3ra.wave.commands.commands.*;
import be.kod3ra.wave.listener.*;
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
    public static final String ANSI_RESET = "\u001b[0m";
    public static final String ANSI_YELLOW = "\u001b[33m";
    public static final String ANSI_BOLD = "\u001b[1m";
    private static Wave instance;
    private final UserData userData = new UserData();

    public static Wave getInstance() {
        return instance;
    }

    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings().checkForUpdates(false).bStats(false);
        PacketEvents.getAPI().load();
    }

    public void onEnable() {
        instance = this;
        Config.getInstance().load();
        PacketEvents.getAPI().getEventManager().registerListener(new UserPacketListener());
        PacketEvents.getAPI().init();
        this.getServer().getPluginManager().registerEvents(new TeleportListener(), this);
        this.getServer().getPluginManager().registerEvents(new RespawnListener(), this);
        this.getServer().getPluginManager().registerEvents(new DamageListener(), this);
        this.getServer().getPluginManager().registerEvents(new MineListener(), this);
        this.getServer().getPluginManager().registerEvents(new Macro(), this);
        new Latency();
        HighPing pingChecker = new HighPing();
        pingChecker.startChecking();
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerConnectionListener(), Wave.getInstance());
        this.getCommand("wave").setExecutor(new WaveCMD());
        this.getCommand("wavehelp").setExecutor(new WaveHelpCMD(Config.getInstance()));
        this.getCommand("wavegui").setExecutor(new WaveGuiCMD(this));
        this.getCommand("waveclient").setExecutor(new WaveClientCMD());
        this.getCommand("wavenotify").setExecutor(new WaveNotifyCMD(this));
        this.getCommand("waveserver").setExecutor(new WaveServerCMD(Config.getInstance()));
        this.getCommand("wavekick").setExecutor(new WaveKickCMD(this));
        this.getCommand("waveban").setExecutor(new WaveBanCMD(this));
        this.getCommand("wavetempban").setExecutor(new WaveTempBanCMD(this));
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

    public UserData getUserData() {
        return this.userData;
    }
}

