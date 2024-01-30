package be.kod3ra.wave.checks;

import be.kod3ra.wave.packet.WrappedPacket;
import be.kod3ra.wave.user.User;

public interface CheckMethod {
    void onPacket(User var1, WrappedPacket var2);
}

