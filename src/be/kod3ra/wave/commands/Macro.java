/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.CommandSender
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerCommandPreprocessEvent
 */
package be.kod3ra.wave.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class Macro
implements Listener {
    @EventHandler
    public void onCommandPreProcess(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().toLowerCase();
        if (command.startsWith("/wave help")) {
            event.setCancelled(true);
            event.setMessage("/wavehelp");
            Bukkit.dispatchCommand((CommandSender)event.getPlayer(), (String)"wavehelp");
        } else if (command.startsWith("/wave gui")) {
            event.setCancelled(true);
            event.setMessage("/wavegui" + command.substring("/wave gui".length()));
            Bukkit.dispatchCommand((CommandSender)event.getPlayer(), (String)("wavegui" + command.substring("/wave gui".length())));
        } else if (command.startsWith("/wave client")) {
            event.setCancelled(true);
            event.setMessage("/waveclient" + command.substring("/wave client".length()));
            Bukkit.dispatchCommand((CommandSender)event.getPlayer(), (String)("waveclient" + command.substring("/wave client".length())));
        } else if (command.startsWith("/wave notify")) {
            event.setCancelled(true);
            event.setMessage("/wavenotify" + command.substring("/wave notify".length()));
            Bukkit.dispatchCommand((CommandSender)event.getPlayer(), (String)("wavenotify" + command.substring("/wave notify".length())));
        } else if (command.startsWith("/wave server")) {
            event.setCancelled(true);
            event.setMessage("/waveserver" + command.substring("/wave server".length()));
            Bukkit.dispatchCommand((CommandSender)event.getPlayer(), (String)("waveserver" + command.substring("/wave server".length())));
        } else if (command.startsWith("/wave ban")) {
            event.setCancelled(true);
            event.setMessage("/waveban" + command.substring("/wave ban".length()));
            Bukkit.dispatchCommand((CommandSender)event.getPlayer(), (String)("waveban" + command.substring("/wave ban".length())));
        } else if (command.startsWith("/wave tempban")) {
            event.setCancelled(true);
            event.setMessage("/wavetempban" + command.substring("/wave tempban".length()));
            Bukkit.dispatchCommand((CommandSender)event.getPlayer(), (String)("wavetempban" + command.substring("/wave tempban".length())));
        } else if (command.startsWith("/wave kick")) {
            event.setCancelled(true);
            event.setMessage("/wavekick" + command.substring("/wave kick".length()));
            Bukkit.dispatchCommand((CommandSender)event.getPlayer(), (String)("wavekick" + command.substring("/wave kick".length())));
        }
    }
}

