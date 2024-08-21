package dev.magyul.network.packets.s2c;

import dev.magyul.network.IPacket;
import dev.magyul.network.NetworkClientInitializer;
import dev.magyul.network.PacketType;
import net.minecraft.network.RegistryByteBuf;

public record PickupReachS2CPacket(int reach) implements IPacket {
    public static final PacketType<PickupReachS2CPacket> TYPE = PacketType.create("pickup_reach", PickupReachS2CPacket::new);

    public PickupReachS2CPacket(RegistryByteBuf buf) {
        this(buf.readInt());
    }

    @Override
    public void write(RegistryByteBuf buf) {
        buf.writeInt(reach);
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
