package dev.magyul.network;

import dev.magyul.MTWMod;
import dev.magyul.util.ServerUtil;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public record KeyInputC2SPacket(int key, boolean released) implements FabricPacket {
    public static final PacketType<KeyInputC2SPacket> TYPE = PacketType.create(new Identifier(MTWMod.ID, "key_input"), KeyInputC2SPacket::new);

    public KeyInputC2SPacket(PacketByteBuf buf) {
        this(buf.readInt(), buf.readBoolean());
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(key);
        buf.writeBoolean(released);
    }

    public void receive(ServerPlayerEntity player, PacketSender ignoredSender) {
        if (player != null) {
            ServerUtil.playerKey.put(player.getUuid(), key, !released);
        }
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
