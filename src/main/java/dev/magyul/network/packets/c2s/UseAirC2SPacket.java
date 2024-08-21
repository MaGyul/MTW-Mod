package dev.magyul.network.packets.c2s;

import dev.magyul.MTWMod;
import dev.magyul.network.IPacket;
import dev.magyul.network.PacketType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public record UseAirC2SPacket() implements IPacket {
    public static final PacketType<UseAirC2SPacket> TYPE = PacketType.create("use_air", UseAirC2SPacket::new);

    public UseAirC2SPacket(RegistryByteBuf buf) {
        this();
    }

    @Override
    public void write(RegistryByteBuf buf) {
    }

    @Override
    public void handle(Context context) {
        if (context.side().isServer() && context.player() instanceof ServerPlayerEntity player) {
            MTWMod.onCarryUse(player);
        }
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
