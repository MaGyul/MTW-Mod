package dev.magyul.mixin.client.network;

import com.llamalad7.mixinextras.sugar.Local;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.network.LegacyServerPinger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LegacyServerPinger.class)
public class LegacyServerPingerMixin {
    @Inject(method = "channelActive", at = @At(value = "INVOKE", target = "Lio/netty/buffer/ByteBuf;writeByte(I)Lio/netty/buffer/ByteBuf;", ordinal = 0, shift = At.Shift.AFTER, remap = false))
    private void channelActive(ChannelHandlerContext context, CallbackInfo cb, @Local ByteBuf buf) {
        buf.writeByte(Byte.MAX_VALUE);
    }
}
