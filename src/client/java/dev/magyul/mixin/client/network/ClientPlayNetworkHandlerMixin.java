package dev.magyul.mixin.client.network;

import dev.magyul.api.ChunkDataPacketAccessor;
import dev.magyul.data.WorldData;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Shadow private ClientWorld world;

    @SuppressWarnings("RedundantCast")
    @Inject(method = "onChunkData", at = @At("TAIL"))
    private void onChunkData(ChunkDataS2CPacket packet, CallbackInfo ci) {
        var x = packet.getX();
        var z = packet.getZ();
        var data = ((ChunkDataPacketAccessor) packet).mtwmod$chunkData();
        WorldData.get(world).getChunkData(x, z).readNbt(data);
    }
}
