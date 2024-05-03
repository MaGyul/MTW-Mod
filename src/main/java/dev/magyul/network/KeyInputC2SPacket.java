package dev.magyul.network;

import dev.magyul.util.ServerUtil;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;

public record KeyInputC2SPacket(int key, boolean released) implements CustomPayload {

    public KeyInputC2SPacket(PacketByteBuf buf) {
        this(buf.readInt(), buf.readBoolean());
    }

    public void write(PacketByteBuf buf) {
        buf.writeInt(key);
        buf.writeBoolean(released);
    }

    public void receive(ServerPlayNetworking.Context context) {
        var player = context.player();
        if (player != null) {
            ServerUtil.playerKey.put(player.getUuid(), key, !released);
        }
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return Namespaces.KEY_INPUT;
    }
}
