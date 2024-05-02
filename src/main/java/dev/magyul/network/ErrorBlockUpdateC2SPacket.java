package dev.magyul.network;

import dev.magyul.MTWMod;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public record ErrorBlockUpdateC2SPacket(Hand hand, int lightLevel) implements FabricPacket {
    public static final PacketType<ErrorBlockUpdateC2SPacket> TYPE = PacketType.create(new Identifier(MTWMod.ID, "error_block_update"), ErrorBlockUpdateC2SPacket::new);

    public ErrorBlockUpdateC2SPacket(PacketByteBuf buf) {
        this(buf.readEnumConstant(Hand.class), buf.readInt());
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeEnumConstant(hand);
        buf.writeInt(lightLevel);
    }

    public void receive(ServerPlayerEntity player, PacketSender ignoredSender) {
        if (player != null) {
            var stack = player.getStackInHand(hand);
            if (!stack.isEmpty()) {
                setLightOnStack(stack, lightLevel);
            }
        }
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    private static void setLightOnStack(ItemStack item, int lightLevel) {
        if (lightLevel != 0) {
            NbtCompound tag = new NbtCompound();
            tag.putInt("level", lightLevel);
            item.setSubNbt("BlockStateTag", tag);
        } else {
            item.removeSubNbt("BlockStateTag");
        }
    }
}
