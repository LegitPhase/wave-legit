package be.kod3ra.wave.user.engine;

import be.kod3ra.wave.packet.WrappedPacket;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerPositionAndRotation;

public class MovementEngine {
    private double lastX;
    private double lastY;
    private double lastZ;
    private double lastDeltaX;
    private double lastDeltaY;
    private double lastDeltaZ;
    private double lastDeltaXZ;

    public void updateCoordinates(WrappedPacket wrappedPacket) {
        if ((wrappedPacket.isMovingAndRotation() || wrappedPacket.isMoving() || wrappedPacket.isFlying()) && wrappedPacket.getPacketReceiveEvent() != null) {
            WrapperPlayClientPlayerFlying positionPacket = new WrapperPlayClientPlayerFlying(wrappedPacket.getPacketReceiveEvent());
            Location location = positionPacket.getLocation();
            double currentX = location.getX();
            double currentY = location.getY();
            double currentZ = location.getZ();
            double deltaX = currentX - this.lastX;
            double deltaY = currentY - this.lastY;
            double deltaZ = currentZ - this.lastZ;
            this.lastX = currentX;
            this.lastY = currentY;
            this.lastZ = currentZ;
            this.lastDeltaX = deltaX;
            this.lastDeltaY = deltaY;
            this.lastDeltaZ = deltaZ;
        }
    }

    public double getDeltaXZ(WrappedPacket wrappedPacket) {
        if ((wrappedPacket.isMovingAndRotation() || wrappedPacket.isMoving()) && wrappedPacket.getPacketReceiveEvent() != null) {
            WrapperPlayClientPlayerPositionAndRotation positionPacket = new WrapperPlayClientPlayerPositionAndRotation(wrappedPacket.getPacketReceiveEvent());
            Location location = positionPacket.getLocation();
            double currentX = location.getX();
            double currentZ = location.getZ();
            double deltaX = currentX - this.lastX;
            double deltaZ = currentZ - this.lastZ;
            this.lastX = currentX;
            this.lastZ = currentZ;
            return Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        }
        return 0.0;
    }

    public double getDeltaY() {
        return this.lastDeltaY;
    }

    public double getDeltaZ() {
        return this.lastDeltaZ;
    }

    public double getDeltaX() {
        return this.lastDeltaX;
    }
}

