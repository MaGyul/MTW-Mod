package dev.magyul.network.packets.c2s;

import dev.magyul.network.IPacket;
import dev.magyul.network.PacketType;
import dev.magyul.util.ServerUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public record KeyInputC2SPacket(int key, boolean released) implements IPacket {
    public static final PacketType<KeyInputC2SPacket> TYPE = PacketType.create("key_input", KeyInputC2SPacket::new);

    public KeyInputC2SPacket(RegistryByteBuf buf) {
        this(buf.readInt(), buf.readBoolean());
    }

    @Override
    public void write(RegistryByteBuf buf) {
        buf.writeInt(key);
        buf.writeBoolean(released);
    }

    @Override
    public void handle(Context context) {
        if (context.side().isServer() && context.player() instanceof ServerPlayerEntity player) {
            ServerUtil.playerKey.put(player.getUuid(), key, !released);
        }
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
