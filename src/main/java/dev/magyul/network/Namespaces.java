package dev.magyul.network;

import dev.magyul.util.NetworkUtil;
import net.minecraft.network.packet.CustomPayload;

public class Namespaces {
    // Client to Server
    public static final CustomPayload.Id<AttackAirC2SPacket> ATTACK_AIR = NetworkUtil.createId("attack_air");
    public static final CustomPayload.Id<ErrorBlockUpdateC2SPacket> ERROR_BLOCK_UPDATE = NetworkUtil.createId("error_block_update");
    public static final CustomPayload.Id<KeyInputC2SPacket> KEY_INPUT = NetworkUtil.createId("key_input");
    public static final CustomPayload.Id<PickupItemC2SPacket> PICKUP_ITEM = NetworkUtil.createId("pickup_item");
    public static final CustomPayload.Id<UseAirC2SPacket> USE_AIR = NetworkUtil.createId("use_air");

    // Server to Client
    public static final CustomPayload.Id<UpdateAllMicS2CPacket> UPDATE_ALL_MIC = NetworkUtil.createId("update_all_mic");
    public static final CustomPayload.Id<PickupReachS2CPacket> PICKUP_REACH = NetworkUtil.createId("pickup_reach");
}
