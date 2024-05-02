package dev.magyul.mixin.network;

import dev.magyul.MTWMod;
import dev.magyul.api.MTWLoginHello;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(LoginHelloC2SPacket.class)
public class LoginHelloC2SPacketMixin implements MTWLoginHello {
    @Unique
    private String mtwmodVersion = null;

    @Inject(method = "<init>(Ljava/lang/String;Ljava/util/UUID;)V", at = @At("RETURN"))
    private void init(String string, UUID uuid, CallbackInfo cb) {
        mtwmodVersion = MTWMod.VERSION;
    }

    @Inject(method = "<init>(Lnet/minecraft/network/PacketByteBuf;)V", at = @At("RETURN"))
    private void init(PacketByteBuf buf, CallbackInfo cb) {
        if (buf.readableBytes() > 0) mtwmodVersion = buf.readString();
        else mtwmodVersion = null;
    }

    @Inject(method = "write", at = @At("RETURN"))
    private void write(PacketByteBuf buf, CallbackInfo cb) {
        buf.writeString(mtwmodVersion);
    }

    @Override
    public String fabric$mtwmodVersion() {
        return mtwmodVersion;
    }
}
