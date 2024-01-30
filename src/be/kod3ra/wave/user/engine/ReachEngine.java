package be.kod3ra.wave.user.engine;

import org.bukkit.entity.Player;

public class ReachEngine {
    public double calculateReach(Player attacker, Player target) {
        return this.calculateReach(attacker.getLocation().getX(), attacker.getLocation().getZ(), target.getLocation().getX(), target.getLocation().getZ());
    }

    private double calculateReach(double startX, double startZ, double endX, double endZ) {
        double reachDistance = this.getHorizontalSpeed(startX, startZ, endX, endZ);
        return reachDistance;
    }

    private double getHorizontalSpeed(double startX, double startZ, double endX, double endZ) {
        double deltaX = endX - startX;
        double deltaZ = endZ - startZ;
        double distanceSquared = deltaX * deltaX + deltaZ * deltaZ;
        return Math.sqrt(distanceSquared);
    }
}

