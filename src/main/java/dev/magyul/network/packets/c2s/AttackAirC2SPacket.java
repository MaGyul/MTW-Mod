package dev.magyul.network.packets.c2s;

import dev.magyul.events.PlayerInteractEvents;
import dev.magyul.network.IPacket;
import dev.magyul.network.PacketType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public record AttackAirC2SPacket(BlockPos pos) implements IPacket {
    public static final PacketType<AttackAirC2SPacket> TYPE = PacketType.create("attack_air", AttackAirC2SPacket::new);

    public AttackAirC2SPacket(RegistryByteBuf buf) {
        this(buf.readBlockPos());
    }

    @Override
    public void write(RegistryByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    public void handle(Context context) {
        if (context.side().isServer() && context.player() instanceof ServerPlayerEntity player) {
            PlayerInteractEvents.ATTACK_AIR_EVENT.invoker().attack(player);
        }
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
