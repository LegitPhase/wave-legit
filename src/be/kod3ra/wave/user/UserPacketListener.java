package be.kod3ra.wave.user;

import be.kod3ra.wave.Wave;
import be.kod3ra.wave.packet.WrappedPacket;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;

import java.util.UUID;

public final class UserPacketListener
        extends PacketListenerAbstract {
    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getUser() == null || event.getUser().getUUID() == null) {
            return;
        }
        UUID uuid = event.getUser().getUUID();
        User user = Wave.getInstance().getUserData().getUser(uuid);
        if (user == null) {
            return;
        }
        PacketTypeCommon packetTypeCommon = event.getPacketType();
        int packetId = event.getPacketId();
        long timeStamp = event.getTimestamp();
        WrappedPacket wrappedPacket = new WrappedPacket(event, packetTypeCommon, packetId, timeStamp);
        user.getCheckManager().getClassToInstanceMap().values().forEach(check -> check.onPacket(user, wrappedPacket));
    }
}

