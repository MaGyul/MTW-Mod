package dev.magyul.network;

import dev.magyul.blocks.ErrorBlock;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Hand;

public record ErrorBlockUpdateC2SPacket(Hand hand, int lightLevel) implements CustomPayload {

    public ErrorBlockUpdateC2SPacket(PacketByteBuf buf) {
        this(buf.readEnumConstant(Hand.class), buf.readInt());
    }

    public void write(PacketByteBuf buf) {
        buf.writeEnumConstant(hand);
        buf.writeInt(lightLevel);
    }

    public void receive(ServerPlayNetworking.Context context) {
        var player = context.player();
        if (player != null) {
            var stack = player.getStackInHand(hand);
            if (!stack.isEmpty()) {
                ErrorBlock.setLightOnStack(stack, lightLevel);
            }
        }
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return Namespaces.ERROR_BLOCK_UPDATE;
    }
}
