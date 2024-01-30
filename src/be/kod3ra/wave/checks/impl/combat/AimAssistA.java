/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package be.kod3ra.wave.checks.impl.combat;

import be.kod3ra.wave.checks.Check;
import be.kod3ra.wave.checks.impl.CheckInfo;
import be.kod3ra.wave.packet.WrappedPacket;
import be.kod3ra.wave.user.User;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@CheckInfo(name = "AIM_ASSIST")
public final class AimAssistA
        extends Check {
    private final Map<Player, Long> lastClickTimes = new HashMap<Player, Long>();

    @Override
    public void onPacket(User user, WrappedPacket wrappedPacket) {
        Player player = user.getPlayer();
        if (!wrappedPacket.isAttacking() || wrappedPacket.isMovingAndRotation()) {
            // empty if block
        }
    }
}

