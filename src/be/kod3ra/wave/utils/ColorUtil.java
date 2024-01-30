/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.md_5.bungee.api.ChatColor
 */
package be.kod3ra.wave.utils;

import net.md_5.bungee.api.ChatColor;

public final class ColorUtil {
    public static String format(String textToTranslate) {
        return ChatColor.translateAlternateColorCodes('&', textToTranslate);
    }
}

