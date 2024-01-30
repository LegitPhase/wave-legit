/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package be.kod3ra.wave.user;

import be.kod3ra.wave.user.User;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;

public class UserManager {
    private final Map<UUID, User> userMap = new HashMap<UUID, User>();

    public User getUser(UUID uuid) {
        return this.userMap.get(uuid);
    }

    public void addUser(User user) {
        this.userMap.put(user.getUUID(), user);
    }

    public void removeUser(UUID uuid) {
        this.userMap.remove(uuid);
    }

    public User getUser(Player player) {
        UUID uuid = player.getUniqueId();
        User user = this.getUser(uuid);
        if (user == null) {
            user = new User(uuid);
            this.addUser(user);
        }
        return user;
    }
}

