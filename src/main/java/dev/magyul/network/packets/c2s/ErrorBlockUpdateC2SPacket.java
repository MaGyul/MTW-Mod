package dev.magyul.network.packets.c2s;

import dev.magyul.blocks.ErrorBlock;
import dev.magyul.network.IPacket;
import dev.magyul.network.PacketType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

public record ErrorBlockUpdateC2SPacket(Hand hand, int lightLevel) implements IPacket {
    public static final PacketType<ErrorBlockUpdateC2SPacket> TYPE = PacketType.create("error_block_update", ErrorBlockUpdateC2SPacket::new);

    public ErrorBlockUpdateC2SPacket(RegistryByteBuf buf) {
        this(buf.readEnumConstant(Hand.class), buf.readInt());
    }

    @Override
    public void write(RegistryByteBuf buf) {
        buf.writeEnumConstant(hand);
        buf.writeInt(lightLevel);
    }

    @Override
    public void handle(Context context) {
        if (context.side().isServer() && context.player() instanceof ServerPlayerEntity player) {
            var stack = player.getStackInHand(hand);
            if (!stack.isEmpty()) {
                ErrorBlock.setLightOnStack(stack, lightLevel);
            }
        }
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
