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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkSerializer.class)
public class ChunkSerializerMixin {
    @Unique
    private static final String notting = "notting";
    @Unique
    private static final String earth_wall = "earth_wall";
    @Unique
    private static final String spruce_plate_wall = "spruce_plate_wall";
    @Unique
    private static final String[] facings = new String[]{"east", "north", "south", "west"};

    @Unique
    private static void updateProperty(NbtCompound nbt, String key, String setVal) {
        var value = nbt.getString(key);
        if (Boolean.parseBoolean(value)) {
            nbt.putString(key, setVal);
        } else {
            nbt.putString(key, notting);
        }
    }

    @Redirect(method = "deserialize", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtCompound;getCompound(Ljava/lang/String;)Lnet/minecraft/nbt/NbtCompound;", ordinal = 1))
    private static NbtCompound unableBlocksUpdate(NbtCompound instance, String key) {
        var nbt = instance.getCompound(key);
        var palette = nbt.getList("palette", 10);
        int updatedBlocks = 0;
        for(int j = 0; j < palette.size(); ++j) {
            var component = palette.getCompound(j);
//            {Name:"mtwmod:spruce_plate_wall_pillar",Properties:{east:"true",north:"true",south:"true",waterlogged:"false",west:"true"}}
            var name = component.getString("Name");
//            {east:"earth_wall",north:"spruce_plate_wall",south:"earth_wall",waterlogged:"false",west:"spruce_plate_wall"}
//            {east:"true",north:"true",south:"true",waterlogged:"false",west:"true"}
            var properties = component.getCompound("Properties");
            if (name.equals("mtwmod:spruce_earth_wall_pillar")) {
                component.putString("Name", "mtwmod:spruce_wall_pillar");
                for (String facing : facings) {
                    updateProperty(properties, facing, earth_wall);
                }
                component.put("Properties", properties);
                updatedBlocks++;
            } else if (name.equals("mtwmod:spruce_plate_wall_pillar")) {
                component.putString("Name", "mtwmod:spruce_wall_pillar");
                for (String facing : facings) {
                    updateProperty(properties, facing, spruce_plate_wall);
                }
                component.put("Properties", properties);
                updatedBlocks++;
            }
        }
        if (updatedBlocks != 0) {
            MTWMod.LOGGER.info("[Chunk] 사용 할 수 없는 블록 {}개 변경됨", updatedBlocks);
        }
        return nbt;
    }

    @Inject(method = "deserialize", at = @At("RETURN"))
    private static void deserialize(ServerWorld world, PointOfInterestStorage poiStorage, ChunkPos chunkPos, NbtCompound nbt, CallbackInfoReturnable<ProtoChunk> cb) {
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
