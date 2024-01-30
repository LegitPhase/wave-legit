package be.kod3ra.wave.utils;

import org.bukkit.entity.Player;

public class PingUtil {
    public static int getPing(Player player) {
        try {
            Object craftPlayer = player.getClass().getMethod("getHandle", new Class[0]).invoke(player, new Object[0]);
            int ping = (Integer)craftPlayer.getClass().getField("ping").get(craftPlayer);
            return ping;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            player.sendMessage("owo");
            return -1;
        }
    }
}

