package dev.magyul.network.packets.s2c;

import dev.magyul.network.IPacket;
import dev.magyul.network.NetworkClientInitializer;
import dev.magyul.network.PacketType;
import net.minecraft.network.RegistryByteBuf;

public record UpdateAllMicS2CPacket(boolean value) implements IPacket {
    public static final PacketType<UpdateAllMicS2CPacket> TYPE = PacketType.create("update_all_mic", UpdateAllMicS2CPacket::new);

    public UpdateAllMicS2CPacket(RegistryByteBuf buf) {
        this(buf.readBoolean());
    }

    @Override
    public void write(RegistryByteBuf buf) {
        buf.writeBoolean(value);
    }

    @Override
    public void handle(Context context) {
        NetworkClientInitializer.callClient(this, context);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
