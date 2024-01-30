package be.kod3ra.wave.user;

import be.kod3ra.wave.Wave;
import be.kod3ra.wave.checks.CheckManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class User {
    private final UUID uuid;
    private final Player player;
    private final String name;
    private final CheckManager checkManager;
    private int packetCount;
    private long lastXRayDetectionTime;

    public User(UUID uuid) {
        this.uuid = uuid;
        this.player = this.getPlayer();
        this.name = this.player.getName();
        this.checkManager = new CheckManager(Wave.getInstance());
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    public String getName() {
        return this.name;
    }

    public CheckManager getCheckManager() {
        return this.checkManager;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public long getLastXRayDetectionTime() {
        return this.lastXRayDetectionTime;
    }

    public void setLastXRayDetectionTime(long time) {
        this.lastXRayDetectionTime = time;
    }

    public UserData getUserData() {
        return Wave.getInstance().getUserData();
    }

    public int getPacketCount() {
        return this.packetCount;
    }

    public void setPacketCount(int packetCount) {
        this.packetCount = packetCount;
    }
}

