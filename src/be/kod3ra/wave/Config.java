/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.YamlConfiguration
 */
package be.kod3ra.wave;

import be.kod3ra.wave.Wave;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
    private static Config instance = new Config();
    private File file;
    private FileConfiguration config;

    private Config() {
    }

    public static Config getInstance() {
        return instance;
    }

    public void load() {
        Wave plugin = Wave.getInstance();
        this.file = new File(plugin.getDataFolder(), "config.yml");
        if (!this.file.exists()) {
            plugin.saveResource("config.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration((File)this.file);
    }

    public void save() {
        try {
            this.config.save(this.file);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void set(String path, Object value) {
        this.config.set(path, value);
        this.save();
    }

    public FileConfiguration getConfig() {
        return this.config;
    }
}

