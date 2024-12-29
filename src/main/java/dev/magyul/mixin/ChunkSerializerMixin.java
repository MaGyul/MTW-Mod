package dev.magyul.mixin;

import dev.magyul.MTWMod;
import dev.magyul.data.WorldData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkSerializer.class)
public class ChunkSerializerMixin {

    @Inject(method = "deserialize", at = @At("RETURN"))
    private static void deserialize(ServerWorld world, PointOfInterestStorage poiStorage, ChunkPos chunkPos, NbtCompound nbt, CallbackInfoReturnable<ProtoChunk> cir) {
        if (nbt.contains(MTWMod.ID)) {
            var worldData = WorldData.get(world);
            worldData.getChunkData(chunkPos).readNbt(nbt.getCompound(MTWMod.ID));
        }
    }

    @Inject(method = "serialize", at = @At("RETURN"))
    private static void serialize(ServerWorld world, Chunk chunk, CallbackInfoReturnable<NbtCompound> cb) {
        var worldData = WorldData.get(world);
        var nbt = cb.getReturnValue();
        nbt.put(MTWMod.ID, worldData.getChunkData(chunk.getPos()).toNbt());
    }
}
