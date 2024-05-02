package dev.magyul.mixin.client.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.network.LegacyServerPinger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LegacyServerPinger.class)
public class LegacyServerPingerMixin {
    @Inject(method = "channelActive", at = @At(value = "INVOKE", target = "Lio/netty/buffer/ByteBuf;writeByte(I)Lio/netty/buffer/ByteBuf;", ordinal = 1, shift = At.Shift.BEFORE, remap = false), locals = LocalCapture.CAPTURE_FAILHARD)
    private void channelActive(ChannelHandlerContext context, CallbackInfo cb, ByteBuf buf) {
        buf.writeByte(Byte.MAX_VALUE);
    }
}
