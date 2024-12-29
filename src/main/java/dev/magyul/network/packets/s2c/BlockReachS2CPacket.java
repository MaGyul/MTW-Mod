package dev.magyul.network.packets.s2c;

import dev.magyul.network.IPacket;
import dev.magyul.network.NetworkClientInitializer;
import dev.magyul.util.NetworkUtil;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;

public record BlockReachS2CPacket(int reach) implements IPacket {
    public static final PacketType<BlockReachS2CPacket> TYPE = NetworkUtil.create("block_reach", BlockReachS2CPacket::new);

    public BlockReachS2CPacket(PacketByteBuf buf) {
        this(buf.readInt());
    }

    @Override
    public void write(PacketByteBuf buf) {
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
