package dev.magyul.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import static dev.magyul.network.Namespaces.*;

public class SNetwork {
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(KEY_INPUT, KeyInputC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(ERROR_BLOCK_UPDATE, ErrorBlockUpdateC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(ATTACK_AIR, AttackAirC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(USE_AIR, UseAirC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(PICKUP_ITEM, PickupItemC2SPacket::receive);
    }
}
