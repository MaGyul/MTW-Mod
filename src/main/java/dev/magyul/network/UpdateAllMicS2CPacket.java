package dev.magyul.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;

public record UpdateAllMicS2CPacket(boolean value) implements CustomPayload {

    public UpdateAllMicS2CPacket(PacketByteBuf buf) {
        this(buf.readBoolean());
    }

    public void write(PacketByteBuf buf) {
        buf.writeBoolean(value);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return Namespaces.UPDATE_ALL_MIC;
    }
}
