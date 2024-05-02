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

public record AttackAirC2SPacket(BlockPos pos) implements FabricPacket {
    public static final PacketType<AttackAirC2SPacket> TYPE = PacketType.create(new Identifier(MTWMod.ID, "attack_air"), AttackAirC2SPacket::new);

    public AttackAirC2SPacket(PacketByteBuf buf) {
        this(buf.readBlockPos());
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    public void receive(ServerPlayerEntity player, PacketSender ignoredSender) {
        if (player != null && PlayerData.hasCarryState(player)) {
            if (player.age == PlayerData.lastTick(player)) return;
            var world = player.getServerWorld();
            var dir = ServerUtil.getDirectionPos(player);
            var carry = PlayerData.getCarryState(player);
            var blockEntityNBT = PlayerData.getCarryTileNBT(player);
            var entity = createEntity(world, pos.getX(), pos.getY() + 1, pos.getZ(), carry.contains(Properties.WATERLOGGED) ? carry.with(Properties.WATERLOGGED, false) : carry);
            entity.blockEntityData = blockEntityNBT;
            entity.setVelocity(dir.multiply(1.5));
            PlayerData.setCarryState(player, null, null);
            player.getInventory().armor.set(3, Items.AIR.getDefaultStack());
            player.currentScreenHandler.sendContentUpdates();
        }
    }

    private FallingBlockEntity createEntity(World world, double x, double y, double z, BlockState block) {
        var entity = new FallingBlockEntity(EntityType.FALLING_BLOCK, world);
        entity.block = block;
        entity.intersectionChecked = true;
        entity.setPosition(x, y, z);
        entity.setVelocity(Vec3d.ZERO);
        entity.prevX = x;
        entity.prevY = y;
        entity.prevZ = z;
        entity.timeFalling = 1;

        world.spawnEntity(entity);
        return entity;
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
