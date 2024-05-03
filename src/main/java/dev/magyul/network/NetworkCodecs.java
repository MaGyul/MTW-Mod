package dev.magyul.network;

import dev.magyul.util.NetworkUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;

public class NetworkCodecs {
    // Client to Server
    public static PacketCodec<PacketByteBuf, UseAirC2SPacket> USE_AIR_CODEC;
    public static PacketCodec<PacketByteBuf, PickupItemC2SPacket> PICKUP_ITEM_CODEC;
    public static PacketCodec<PacketByteBuf, KeyInputC2SPacket> KEY_INPUT_CODEC;
    public static PacketCodec<PacketByteBuf, ErrorBlockUpdateC2SPacket> ERROR_BLOCK_UPDATE_CODEC;
    public static PacketCodec<PacketByteBuf, AttackAirC2SPacket> ATTACK_AIR_CODEC;

    // Server to Client
    public static PacketCodec<PacketByteBuf, UpdateAllMicS2CPacket> UPDATE_ALL_MIC_CODEC;
    public static PacketCodec<PacketByteBuf, PickupReachS2CPacket> PICKUP_REACH_CODEC;

    public static void register() {
        // Client to Server
        USE_AIR_CODEC = NetworkUtil.createC2S(
                Namespaces.USE_AIR, UseAirC2SPacket::write, UseAirC2SPacket::new);
        PICKUP_ITEM_CODEC = NetworkUtil.createC2S(
                Namespaces.PICKUP_ITEM, PickupItemC2SPacket::write, PickupItemC2SPacket::new);
        KEY_INPUT_CODEC = NetworkUtil.createC2S(
                Namespaces.KEY_INPUT, KeyInputC2SPacket::write, KeyInputC2SPacket::new);
        ERROR_BLOCK_UPDATE_CODEC = NetworkUtil.createC2S(
                Namespaces.ERROR_BLOCK_UPDATE, ErrorBlockUpdateC2SPacket::write, ErrorBlockUpdateC2SPacket::new);
        ATTACK_AIR_CODEC = NetworkUtil.createC2S(
                Namespaces.ATTACK_AIR, AttackAirC2SPacket::write, AttackAirC2SPacket::new);

        // Server to Client
        UPDATE_ALL_MIC_CODEC = NetworkUtil.createS2C(
                Namespaces.UPDATE_ALL_MIC, UpdateAllMicS2CPacket::write, UpdateAllMicS2CPacket::new);
        PICKUP_REACH_CODEC = NetworkUtil.createS2C(
                Namespaces.PICKUP_REACH, PickupReachS2CPacket::write, PickupReachS2CPacket::new);
    }
}
