package dev.magyul.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class RegionRoot {
    private final BlockPos pos;
    private final float yaw;

    public RegionRoot(BlockPos pos, float yaw) {
        this.pos = pos;
        this.yaw = yaw;
    }

    public static RegionRoot fromPlayer(ServerPlayerEntity player) {
        return new RegionRoot(BlockPos.ofFloored(player.getPos()), player.getYaw());
    }

    public static RegionRoot fromNbt(NbtCompound nbt) {
        return new RegionRoot(BlockPos.fromLong(nbt.getLong("pos")), nbt.getFloat("yaw"));
    }

    public void teleport(ServerPlayerEntity player) {
        var center = pos.toCenterPos();
        player.teleport(player.getServerWorld(), center.x, center.y, center.z, yaw, 0);
    }

    public NbtCompound toNbt() {
        var nbt = new NbtCompound();
        nbt.putLong("pos", pos.asLong());
        nbt.putFloat("yaw", yaw);
        return nbt;
    }

}
