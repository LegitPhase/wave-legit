/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package be.kod3ra.wave.utils;

import org.bukkit.entity.Player;

public class ClientUtil {
    public static String getClient(Player player) {
        String brand = player.getListeningPluginChannels().toString().toLowerCase();
        if (brand.contains("lunar") || brand.contains("lunarclient")) {
            return "Lunar Client";
        }
        if (brand.contains("labymod") || brand.contains("labymod3:main")) {
            return "LabyMod";
        }
        if (brand.contains("feather:client") || brand.contains("feather")) {
            return "Feather Client";
        }
        if (brand.contains("badlion") || brand.contains("bdl")) {
            return "Badlion Client";
        }
        if (brand.contains("forge") || brand.contains("fml")) {
            return "Forge";
        }
        if (brand.contains("worlddownloader") || brand.contains("wdl")) {
            return "World Downloader";
        }
        if (brand.contains("optifine") || brand.contains("opt")) {
            return "OptiFine";
        }
        if (brand.contains("fabric")) {
            return "Fabric";
        }
        if (brand.contains("vanilla") || brand.contains("mc") || brand.contains("minecraft")) {
            return "Vanilla";
        }
        if (brand.contains("cheatbreaker")) {
            return "CheatBreaker";
        }
        if (brand.contains("pvplounge")) {
            return "PvPLounge";
        }
        if (brand.contains("geyser")) {
            return "Geyser";
        }
        if (brand.contains("wecui") || brand.contains("wecui")) {
            return "Lunar Client or WorldEditCUI";
        }
        return "N/A";
    }

    public static String getBrand(Player player) {
        String brand = player.getListeningPluginChannels().toString().toLowerCase();
        return brand;
    }
}

