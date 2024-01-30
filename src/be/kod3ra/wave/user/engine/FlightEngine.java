/*
 * Decompiled with CFR 0.152.
 */
package be.kod3ra.wave.user.engine;

import be.kod3ra.wave.packet.WrappedPacket;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

public class FlightEngine {
    private double lastX;
    private double lastY;
    private double lastZ;

    public boolean isFlying(WrappedPacket wrappedPacket) {
        if (wrappedPacket.isFlying() && wrappedPacket.getPacketReceiveEvent() != null) {
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
            return true;
        }
        return false;
    }
}

