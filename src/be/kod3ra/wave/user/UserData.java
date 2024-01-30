/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  org.bukkit.entity.Player
 */
package be.kod3ra.wave.user;

import be.kod3ra.wave.user.User;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;

public final class UserData {
    private final Map<UUID, User> userMap = Maps.newHashMap();
    private final Map<UUID, Long> lastWaterEnterTimes = new HashMap<UUID, Long>();
    private final Map<UUID, Long> joinTimeMap = new HashMap<UUID, Long>();
    private final Map<UUID, Long> lastTeleportTimes = new HashMap<UUID, Long>();
    private final Map<UUID, Long> lastDamageTimeMap = new HashMap<UUID, Long>();
    private final Map<UUID, Long> lastDamageIgnoredTimeMap = new HashMap<UUID, Long>();
    private final Map<UUID, Long> lastAttackTimeMap = new HashMap<UUID, Long>();
    private Map<UUID, Long> lastSneakIgnoreTimes = new HashMap<UUID, Long>();

    public void createUserData(Player player) {
        if (!this.userMap.containsKey(player.getUniqueId())) {
            this.userMap.put(player.getUniqueId(), new User(player.getUniqueId()));
        }
    }

    public void setLastDamageTime(UUID playerId, long time) {
        this.lastDamageTimeMap.put(playerId, time);
    }

    public long getLastDamageTime(UUID playerId) {
        return this.lastDamageTimeMap.getOrDefault(playerId, 0L);
    }

    public void setLastDamageIgnoredTime(UUID playerId, long time) {
        this.lastDamageIgnoredTimeMap.put(playerId, time);
    }

    public long getLastDamageIgnoredTime(UUID playerId) {
        return this.lastDamageIgnoredTimeMap.getOrDefault(playerId, 0L);
    }

    public void setLastAttackTime(UUID playerId, long time) {
        this.lastAttackTimeMap.put(playerId, time);
    }

    public long getLastAttackTime(UUID playerId) {
        return this.lastAttackTimeMap.getOrDefault(playerId, 0L);
    }

    public long getJoinTime(UUID uuid) {
        return this.joinTimeMap.getOrDefault(uuid, 0L);
    }

    public void setJoinTime(UUID uuid, long joinTime) {
        this.joinTimeMap.put(uuid, joinTime);
    }

    public void deleteUserData(Player player) {
        this.userMap.remove(player.getUniqueId());
    }

    public User getUser(Player player) {
        return this.userMap.get(player.getUniqueId());
    }

    public User getUser(UUID uuid) {
        return this.userMap.get(uuid);
    }

    public void setLastSneakIgnoreTime(UUID playerId, long time) {
        this.lastSneakIgnoreTimes.put(playerId, time);
    }

    public long getLastSneakIgnoreTime(UUID playerId) {
        return this.lastSneakIgnoreTimes.getOrDefault(playerId, 0L);
    }

    public long getLastWaterEnterTime(UUID playerUUID) {
        return this.lastWaterEnterTimes.getOrDefault(playerUUID, 0L);
    }

    public void setLastWaterEnterTime(UUID playerUUID, long time) {
        this.lastWaterEnterTimes.put(playerUUID, time);
    }

    public void setLastTeleportTime(UUID playerId, long time) {
        this.lastTeleportTimes.put(playerId, time);
    }

    public long getLastTeleportTime(UUID playerId) {
        return this.lastTeleportTimes.getOrDefault(playerId, 0L);
    }
}

