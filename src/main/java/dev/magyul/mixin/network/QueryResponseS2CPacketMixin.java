package dev.magyul.mixin.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(QueryResponseS2CPacket.class)
public class QueryResponseS2CPacketMixin {
    @Inject(method = "<init>(Lnet/minecraft/network/PacketByteBuf;)V", at = @At("RETURN"))
    private void init(PacketByteBuf buf, CallbackInfo cb) {
        if (buf.readableBytes() > 0) buf.readVarInt();
    }

    @Inject(method = "write", at = @At("RETURN"))
    private void write(PacketByteBuf buf, CallbackInfo cb) {
        buf.writeVarInt(0);
    }
}
