/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package be.kod3ra.wave.packet;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import org.bukkit.entity.Player;

public final class WrappedPacket {
    private final PacketTypeCommon packetTypeCommon;
    private final int packetId;
    private final long timeStamp;
    private PacketSendEvent packetSendEvent;
    private PacketReceiveEvent packetReceiveEvent;
    private Player attacker;
    private Player attackedPlayer;
    private float yaw;
    private float pitch;

    public WrappedPacket(PacketReceiveEvent packetReceiveEvent, PacketTypeCommon packetTypeCommon, int packetId, long timeStamp) {
        this.packetReceiveEvent = packetReceiveEvent;
        this.packetTypeCommon = packetTypeCommon;
        this.packetId = packetId;
        this.timeStamp = timeStamp;
    }

    public WrappedPacket(PacketSendEvent packetSendEvent, PacketTypeCommon packetTypeCommon, int packetId, long timeStamp) {
        this.packetSendEvent = packetSendEvent;
        this.packetTypeCommon = packetTypeCommon;
        this.packetId = packetId;
        this.timeStamp = timeStamp;
    }

    public PacketReceiveEvent getPacketReceiveEvent() {
        return this.packetReceiveEvent;
    }

    public boolean isFlying() {
        return WrapperPlayClientPlayerFlying.isFlying(this.packetTypeCommon);
    }

    public boolean movementEngine() {
        return WrapperPlayClientPlayerFlying.isFlying(this.packetTypeCommon);
    }

    public boolean isMoving() {
        return this.packetTypeCommon.equals(PacketType.Play.Client.PLAYER_POSITION);
    }

    public boolean isMovingAndRotation() {
        return this.packetTypeCommon.equals(PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION);
    }

    public boolean isRotation() {
        return this.packetTypeCommon.equals(PacketType.Play.Client.PLAYER_ROTATION) || this.packetTypeCommon.equals(PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION);
    }

    public boolean isAttacking() {
        return this.packetTypeCommon.equals(PacketType.Play.Client.INTERACT_ENTITY);
    }

    public boolean ArmAnimation() {
        return this.packetTypeCommon.equals(PacketType.Play.Client.ANIMATION);
    }

    public boolean isDigging() {
        return this.packetTypeCommon.equals(PacketType.Play.Client.PLAYER_DIGGING);
    }

    public boolean isPlacing() {
        return this.packetTypeCommon.equals(PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT);
    }

    public boolean UseItem() {
        return this.packetTypeCommon.equals(PacketType.Play.Client.USE_ITEM);
    }

    public boolean allPackets() {
        return this.packetTypeCommon.equals(PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION) || this.packetTypeCommon.equals(PacketType.Play.Client.PLAYER_POSITION) || this.packetTypeCommon.equals(PacketType.Play.Client.ADVANCEMENT_TAB) || this.packetTypeCommon.equals(PacketType.Play.Client.CHAT_PREVIEW) || this.packetTypeCommon.equals(PacketType.Play.Client.CHAT_SESSION_UPDATE) || this.packetTypeCommon.equals(PacketType.Play.Client.CLICK_WINDOW) || this.packetTypeCommon.equals(PacketType.Play.Client.CLICK_WINDOW_BUTTON) || this.packetTypeCommon.equals(PacketType.Play.Client.CLIENT_STATUS) || this.packetTypeCommon.equals(PacketType.Play.Client.CLOSE_WINDOW) || this.packetTypeCommon.equals(PacketType.Play.Client.CRAFT_RECIPE_REQUEST) || this.packetTypeCommon.equals(PacketType.Play.Client.PLAYER_ABILITIES) || this.packetTypeCommon.equals(PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) || this.packetTypeCommon.equals(PacketType.Play.Client.PLAYER_DIGGING) || this.packetTypeCommon.equals(PacketType.Play.Client.PLAYER_FLYING) || this.packetTypeCommon.equals(PacketType.Play.Client.PLAYER_POSITION) || this.packetTypeCommon.equals(PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION) || this.packetTypeCommon.equals(PacketType.Play.Client.PLAYER_ROTATION) || this.packetTypeCommon.equals(PacketType.Play.Client.PLUGIN_MESSAGE) || this.packetTypeCommon.equals(PacketType.Play.Client.PONG) || this.packetTypeCommon.equals(PacketType.Play.Client.QUERY_BLOCK_NBT) || this.packetTypeCommon.equals(PacketType.Play.Client.QUERY_ENTITY_NBT) || this.packetTypeCommon.equals(PacketType.Play.Client.RESOURCE_PACK_STATUS) || this.packetTypeCommon.equals(PacketType.Play.Client.SELECT_TRADE) || this.packetTypeCommon.equals(PacketType.Play.Client.SET_BEACON_EFFECT) || this.packetTypeCommon.equals(PacketType.Play.Client.SET_DIFFICULTY) || this.packetTypeCommon.equals(PacketType.Play.Client.SET_DISPLAYED_RECIPE) || this.packetTypeCommon.equals(PacketType.Play.Client.SET_RECIPE_BOOK_STATE) || this.packetTypeCommon.equals(PacketType.Play.Client.SLOT_STATE_CHANGE) || this.packetTypeCommon.equals(PacketType.Play.Client.SPECTATE) || this.packetTypeCommon.equals(PacketType.Play.Client.STEER_BOAT) || this.packetTypeCommon.equals(PacketType.Play.Client.STEER_VEHICLE) || this.packetTypeCommon.equals(PacketType.Play.Client.TAB_COMPLETE) || this.packetTypeCommon.equals(PacketType.Play.Client.TELEPORT_CONFIRM) || this.packetTypeCommon.equals(PacketType.Play.Client.UPDATE_COMMAND_BLOCK) || this.packetTypeCommon.equals(PacketType.Play.Client.UPDATE_COMMAND_BLOCK_MINECART) || this.packetTypeCommon.equals(PacketType.Play.Client.UPDATE_JIGSAW_BLOCK) || this.packetTypeCommon.equals(PacketType.Play.Client.UPDATE_SIGN) || this.packetTypeCommon.equals(PacketType.Play.Client.USE_ITEM) || this.packetTypeCommon.equals(PacketType.Play.Client.VEHICLE_MOVE) || this.packetTypeCommon.equals(PacketType.Play.Client.WINDOW_CONFIRMATION);
    }
}

