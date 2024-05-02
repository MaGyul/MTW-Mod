package dev.magyul.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class SNetwork {

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(KeyInputC2SPacket.TYPE, KeyInputC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(ErrorBlockUpdateC2SPacket.TYPE, ErrorBlockUpdateC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(AttackAirC2SPacket.TYPE, AttackAirC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(UseAirC2SPacket.TYPE, UseAirC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(PickupItemC2SPacket.TYPE, PickupItemC2SPacket::receive);
    }
}
