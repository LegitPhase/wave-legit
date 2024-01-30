package be.kod3ra.wave.checks.impl.player;

import be.kod3ra.wave.checks.Check;
import be.kod3ra.wave.checks.impl.CheckInfo;
import be.kod3ra.wave.packet.WrappedPacket;
import be.kod3ra.wave.user.User;
import be.kod3ra.wave.user.UserManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

@CheckInfo(name = "XRAY")
public final class XRayA
        extends Check {
    private final UserManager userManager;
    private final Map<UUID, Integer> diamondCounter = new HashMap<UUID, Integer>();
    private final int diamondThreshold = 9;
    private final long detectionInterval = 45000L;

    public XRayA() {
        this.userManager = new UserManager();
    }

    @Override
    public void onPacket(User user, WrappedPacket wrappedPacket) {
        Player player = user.getPlayer();
        if (wrappedPacket.isDigging()) {
            this.handleBlockBreakEvent(user, null);
        }
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player != null) {
            this.handleBlockBreakEvent(this.getUser(player), event);
        }
    }

    private void handleBlockBreakEvent(User user, BlockBreakEvent event) {
        Player player = user.getPlayer();
        UUID playerId = player.getUniqueId();
        Block brokenBlock = player.getTargetBlock((HashSet<Byte>) null, 5);
        if (brokenBlock.getType() == Material.DIAMOND_ORE && event.getBlock().getType() == Material.AIR) {
            this.diamondCounter.put(playerId, this.diamondCounter.getOrDefault(playerId, 0) + 1);
            if (this.diamondCounter.getOrDefault(playerId, 0) >= 9) {
                String debugInfo = "Player mined 9 diamond ores within 45 seconds";
                this.flag(user, "A", "XRay", 1, debugInfo);
                this.diamondCounter.put(playerId, 0);
            }
        }
        if (System.currentTimeMillis() - user.getLastXRayDetectionTime() > 45000L) {
            this.diamondCounter.put(playerId, 0);
            user.setLastXRayDetectionTime(System.currentTimeMillis());
        }
    }

    private User getUser(Player player) {
        return this.userManager.getUser(player);
    }
}

