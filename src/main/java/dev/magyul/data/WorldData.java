package dev.magyul.data;

import dev.magyul.MTWMod;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class WorldData {
    private static final Map<Identifier, WorldData> worldDataCache = new HashMap<>();

    private final Map<Long, ChunkData> chunkDataCache = new HashMap<>();
    private final ServerWorld world;

    public WorldData(ServerWorld world) {
        this.world = world;
    }

    @NotNull
    public ChunkData getChunkData(ChunkPos pos) {
        var chunkLong = pos.toLong();
        return chunkDataCache.computeIfAbsent(chunkLong, (l) -> new ChunkData(world, pos));
    }

    @NotNull
    public ChunkData getChunkData(BlockPos pos) {
        return getChunkData(new ChunkPos(pos));
    }

    public static WorldData get(ServerWorld world) {
        var value = world.getRegistryKey().getValue();
        return worldDataCache.computeIfAbsent(value, (id) -> new WorldData(world));
    }

    public void readNbt(NbtCompound nbt) {
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        return nbt;
    }

    @Override
    public String toString() {
        return world.getRegistryKey().getValue().toString();
    }
}
