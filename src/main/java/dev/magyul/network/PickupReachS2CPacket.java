package dev.magyul.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;

public record PickupReachS2CPacket(int reach) implements CustomPayload {

    public PickupReachS2CPacket(PacketByteBuf buf) {
        this(buf.readInt());
    }

    public void write(PacketByteBuf buf) {
        buf.writeInt(reach);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return Namespaces.PICKUP_REACH;
    }
}
