package dev.magyul.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class WorldData {
    private static final Map<Identifier, WorldData> worldDataCache = new HashMap<>();

    private final Map<Long, ChunkData> chunkDataCache = new HashMap<>();
    private final Map<String, RegionRoot> regionRootMap = new HashMap<>();
    private final World world;

    public WorldData(World world) {
        this.world = world;
    }

    @NotNull
    public ChunkData getChunkData(int x, int z) {
        var chunkLong = ChunkPos.toLong(x, z);
        return chunkDataCache.computeIfAbsent(chunkLong, (l) -> new ChunkData(world, new ChunkPos(x, z)));
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

    public static WorldData get(World world) {
        var value = world.getRegistryKey().getValue();
        return worldDataCache.computeIfAbsent(value, (id) -> new WorldData(world));
    }

    public void setRegionRoot(RegionRoot root, String first, String second) {
        regionRootMap.put(String.format("%s;%s", first, second), root);
    }

    @Nullable
    public RegionRoot getRegionRoot(String first, String second) {
        return regionRootMap.get(String.format("%s;%s", first, second));
    }

    public void readNbt(NbtCompound nbt) {
        regionRootMap.clear();
        if (nbt.contains("regionRoots")) {
            var regionRoots = nbt.getCompound("regionRoots");
            for (var key : regionRoots.getKeys()) {
                try {
                    regionRootMap.put(key, RegionRoot.fromNbt(regionRoots.getCompound(key)));
                } catch (Exception ignored) {}
            }
        }
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound regionRoots = new NbtCompound();
        for (var key : regionRootMap.keySet()) {
            var value = regionRootMap.get(key);
            regionRoots.put(key, value.toNbt());
        }
        nbt.put("regionRoots", regionRoots);
        return nbt;
    }

    @Override
    public String toString() {
        return world.getRegistryKey().getValue().toString();
    }
}
