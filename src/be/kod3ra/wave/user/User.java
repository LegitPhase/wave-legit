/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 */
package be.kod3ra.wave.user;

import be.kod3ra.wave.Wave;
import be.kod3ra.wave.checks.CheckManager;
import be.kod3ra.wave.user.UserData;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class User {
    private String name;
    private final UUID uuid;
    private final Player player;
    private int packetCount;
    private long lastXRayDetectionTime;
    private CheckManager checkManager;

    public User(UUID uuid) {
        this.uuid = uuid;
        this.player = this.getPlayer();
        this.name = this.player.getName();
        this.checkManager = new CheckManager(Wave.getInstance());
    }

    public Player getPlayer() {
        return Bukkit.getPlayer((UUID)this.uuid);
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

