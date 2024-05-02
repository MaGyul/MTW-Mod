package dev.magyul.mixin.network;

import dev.magyul.MTWMod;
import dev.magyul.api.ModInfo;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PingResultS2CPacket.class)
public class PingResultS2CPacketMixin implements ModInfo {
    @Unique
    private String version;

    public PingResultS2CPacketMixin() {
    }

    @Inject(method = "<init>(J)V", at = @At("RETURN"))
    private void init(long startTime, CallbackInfo cb) {
        version = MTWMod.VERSION;
    }

    @Inject(method = "<init>(Lnet/minecraft/network/PacketByteBuf;)V", at = @At("RETURN"))
    private void init(PacketByteBuf buf, CallbackInfo cb) {
        if (buf.readableBytes() > 0) version = buf.readString();
    }

    @Inject(method = "write", at = @At("RETURN"))
    private void write(PacketByteBuf buf, CallbackInfo cb) {
        buf.writeString(version);
    }

    @Override
    public String mtwmod$version() {
        return version;
    }

    @Override
    public void mtwmod$version(String version) {
        this.version = version;
    }
}
