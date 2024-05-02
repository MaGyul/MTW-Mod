package dev.magyul.mixin.voicechat;

import de.maxhenkel.voicechat.voice.common.NetworkMessage;
import de.maxhenkel.voicechat.voice.common.Packet;
import dev.magyul.MTWMod;
import dev.magyul.voicechat.AllSoundPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(NetworkMessage.class)
public class NetworkMessageMixin {
    @Shadow(remap = false) @Final private static Map<Byte, Class<? extends Packet<?>>> packetRegistry;

    @Inject(method = "<clinit>", at = @At("TAIL"), remap = false)
    private static void injected(CallbackInfo cb) {
        var size = packetRegistry.keySet().size();
        packetRegistry.put((byte)(size + 1), AllSoundPacket.class);
    }
}
