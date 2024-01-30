/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 */
package be.kod3ra.wave.user.utilsengine;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LocationEngine {
    private Location currentLocation = null;
    private Location previousLocation = null;
    private Location previousGroundLocation = null;

    public Location getCurrentLocation(Player player) {
        if (player != null) {
            this.currentLocation = player.getLocation();
        }
        return this.currentLocation;
    }

    public Location getPreviousLocation() {
        return this.previousLocation;
    }

    public Location getPreviousGroundLocation() {
        return this.previousGroundLocation;
    }

    public void updatePreviousLocations(Player player) {
        if (player != null) {
            this.previousLocation = this.currentLocation;
            this.previousGroundLocation = this.getGroundLocation(player);
        }
    }

    private Location getGroundLocation(Player player) {
        Location loc = player.getLocation();
        loc.setY((double)(player.getWorld().getHighestBlockYAt(loc) - 1));
        return loc;
    }
}

