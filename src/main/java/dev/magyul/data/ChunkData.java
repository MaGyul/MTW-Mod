package dev.magyul.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChunkData {
    private final World world;
    private final ChunkPos pos;
    private String first;
    private String second;

    ChunkData(World world, ChunkPos pos) {
        this.world = world;
        this.pos = pos;
    }

    @Nullable
    public String getFirst() {
        return first;
    }

    @Nullable
    public String getSecond() {
        return second;
    }

    public void setRegion(@NotNull String first, @NotNull String second) {
        this.first = first;
        this.second = second;
        update();
    }

    public void deleteRegion() {
        this.first = null;
        this.second = null;
        update();
    }

    public void update() {
        var chunk = world.getChunk(pos.x, pos.z);
        chunk.setNeedsSaving(true);
        if (world instanceof ServerWorld serverWorld) {
            for (var player : serverWorld.getPlayers()) {
                player.networkHandler.sendPacket(new ChunkDataS2CPacket(chunk, world.getLightingProvider(), null, null));
            }
        }
    }

    public void readNbt(NbtCompound nbt) {
        first = null;
        second = null;
        if (nbt.contains("region")) {
            var region = nbt.getCompound("region");
            first = region.getString("first");
            second = region.getString("second");
        }
    }

    public NbtCompound toNbt() {
        var nbt = new NbtCompound();
        if (first != null && second != null) {
            var region = new NbtCompound();
            region.putString("first", first);
            region.putString("second", second);
            nbt.put("region", region);
        } else {
            nbt.remove("region");
        }
        return nbt;
    }

    @Override
    public String toString() {
        return String.format("(%d, %s)", pos.x, pos.z);
    }
}
