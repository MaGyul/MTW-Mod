package dev.magyul.mixin.network;

import dev.magyul.api.ChunkDataPacketAccessor;
import dev.magyul.data.WorldData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.BitSet;

@Mixin(ChunkDataS2CPacket.class)
public class ChunkDataS2CPacketMixin implements ChunkDataPacketAccessor {
    @Unique
    private NbtCompound chunkData;

    @Inject(method = "<init>(Lnet/minecraft/world/chunk/WorldChunk;Lnet/minecraft/world/chunk/light/LightingProvider;Ljava/util/BitSet;Ljava/util/BitSet;)V", at = @At("RETURN"))
    private void init(WorldChunk chunk, LightingProvider lightProvider, BitSet skyBits, BitSet blockBits, CallbackInfo ci) {
        var world = chunk.getWorld();
        chunkData = WorldData.get(world).getChunkData(chunk.getPos()).toNbt();
    }

    @Inject(method = "<init>(Lnet/minecraft/network/PacketByteBuf;)V", at = @At("RETURN"))
    private void init(PacketByteBuf buf, CallbackInfo ci) {
        chunkData = buf.readNbt();
    }

    @Inject(method = "write", at = @At("RETURN"))
    private void write(PacketByteBuf buf, CallbackInfo ci) {
        buf.writeNbt(chunkData);
    }

    @Override
    public NbtCompound mtwmod$chunkData() {
        return chunkData;
    }
}
