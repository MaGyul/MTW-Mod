package dev.magyul.network;

import dev.magyul.MTWMod;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;

public record UseAirC2SPacket() implements CustomPayload {
    public UseAirC2SPacket(PacketByteBuf buf) {
        this();
    }

    public void write(PacketByteBuf buf) {
    }

    public void receive(ServerPlayNetworking.Context context) {
        MTWMod.onCarryUse(context.player());
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return Namespaces.USE_AIR;
    }
}
