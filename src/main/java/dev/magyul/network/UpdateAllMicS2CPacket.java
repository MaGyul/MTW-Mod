package dev.magyul.network;

import dev.magyul.MTWMod;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record UpdateAllMicS2CPacket(boolean value) implements FabricPacket {
    public static final PacketType<UpdateAllMicS2CPacket> TYPE = PacketType.create(new Identifier(MTWMod.ID, "update_all_mic"), UpdateAllMicS2CPacket::new);

    public UpdateAllMicS2CPacket(PacketByteBuf buf) {
        this(buf.readBoolean());
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBoolean(value);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
