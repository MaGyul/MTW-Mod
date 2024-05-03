package dev.magyul.network;

import dev.magyul.events.PlayerInteractEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record AttackAirC2SPacket(BlockPos pos) implements CustomPayload {

    public AttackAirC2SPacket(PacketByteBuf buf) {
        this(buf.readBlockPos());
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    public void receive(ServerPlayNetworking.Context context) {
        var player = context.player();
        PlayerInteractEvents.ATTACK_AIR_EVENT.invoker().attack(player);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return Namespaces.ATTACK_AIR;
    }
}
