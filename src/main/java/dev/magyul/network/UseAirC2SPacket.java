package dev.magyul.network;

import dev.magyul.MTWMod;
import dev.magyul.data.PlayerData;
import dev.magyul.util.ServerUtil;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public record UseAirC2SPacket() implements FabricPacket {
    public static final PacketType<UseAirC2SPacket> TYPE = PacketType.create(new Identifier(MTWMod.ID, "use_air"), UseAirC2SPacket::new);

    public UseAirC2SPacket(PacketByteBuf buf) {
        this();
    }

    @Override
    public void write(PacketByteBuf buf) {
    }

    public void receive(ServerPlayerEntity player, PacketSender ignoredSender) {
        MTWMod.onCarryUse(player);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
