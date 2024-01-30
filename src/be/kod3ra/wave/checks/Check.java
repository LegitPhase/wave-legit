package be.kod3ra.wave.checks;

import be.kod3ra.wave.Wave;
import be.kod3ra.wave.checks.impl.CheckInfo;
import be.kod3ra.wave.user.User;
import be.kod3ra.wave.user.utilsengine.ReliabilityEngine;
import be.kod3ra.wave.utils.ColorUtil;
import be.kod3ra.wave.utils.PingUtil;
import be.kod3ra.wave.utils.TPSUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class Check
        implements CheckMethod {
    public int violations = 0;
    private String checkName;
    private String alertFormat;

    public Check() {
        if (!this.getClass().isAnnotationPresent(CheckInfo.class)) {
            System.out.println("@CheckInfo not found in: " + this.getClass().getSimpleName());
            return;
        }
        CheckInfo checkInfo = this.getClass().getAnnotation(CheckInfo.class);
        this.checkName = checkInfo.name();
        this.alertFormat = Wave.getInstance().getConfig().getString("alerts.format", "\u00a7b\u00a7lWave \u00a7b// \u00a7e%player% \u00a77failed \u00a7e%check% (%type%) \u00a78[\u00a77VL: %violations%\u00a78] \u00a78[\u00a77RL: %reliability%%\u00a78]");
    }

    public void flag(User user, String type, String information, int violations, String debugInfo) {
        Player player = user.getPlayer();
        if (player == null) {
            return;
        }
        String playerName = user.getName();
        int playerPing = PingUtil.getPing(player);
        double[] recentTps = TPSUtil.getRecentTPS();
        double reliability = ReliabilityEngine.calculateReliability(playerPing, recentTps[0]);
        String pingInfo = "\u00a77Ping: " + playerPing + "ms";
        String tpsInfo = "\u00a77TPS: \u00a7f" + String.format("%.2f", recentTps[0]) + " \u00a77(1m), " + String.format("%.2f", recentTps[1]) + " \u00a77(5m), " + String.format("%.2f", recentTps[2]) + " \u00a77(15m)";
        String reliabilityInfo = "\u00a77Reliability: \u00a7f" + reliability + "%";
        String reliabilityNumber = String.valueOf(reliability);
        String message = ColorUtil.format(this.alertFormat.replace("%player%", playerName).replace("%check%", this.checkName).replace("%type%", type).replace("%information%", information).replace("%violations%", String.valueOf(violations)).replace("%reliability%", reliabilityNumber).replace("%debug%", debugInfo));
        TextComponent textComponent = new TextComponent(message);
        BaseComponent[] baseComponents = new ComponentBuilder("\u00a77Informations:\n\n" + pingInfo + "\n" + tpsInfo + "\n\u00a77Debug: \u00a7f" + debugInfo + "\n" + reliabilityInfo).create();
        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, baseComponents);
        textComponent.setHoverEvent(hoverEvent);
        Bukkit.getOnlinePlayers().stream().filter(p -> p.hasPermission("wave.notify")).forEach(p -> p.spigot().sendMessage(textComponent));
        Bukkit.getServer().getConsoleSender().sendMessage(message);
    }
}
