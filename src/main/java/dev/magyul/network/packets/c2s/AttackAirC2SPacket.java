package dev.magyul.network.packets.c2s;

import dev.magyul.events.PlayerInteractEvents;
import dev.magyul.network.IPacket;
import dev.magyul.util.NetworkUtil;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public record AttackAirC2SPacket(BlockPos pos) implements IPacket {
    public static final PacketType<AttackAirC2SPacket> TYPE = NetworkUtil.create("attack_air", AttackAirC2SPacket::new);

    public AttackAirC2SPacket(PacketByteBuf buf) {
        this(buf.readBlockPos());
    }

    @Override
    public void write(PacketByteBuf buf) {
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
