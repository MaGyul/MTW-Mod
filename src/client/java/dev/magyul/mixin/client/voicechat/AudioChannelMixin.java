package dev.magyul.mixin.client.voicechat;

import de.maxhenkel.voicechat.plugins.PluginManager;
import de.maxhenkel.voicechat.voice.client.AudioChannel;
import de.maxhenkel.voicechat.voice.client.ClientVoicechat;
import de.maxhenkel.voicechat.voice.client.PositionalAudioUtils;
import de.maxhenkel.voicechat.voice.client.speaker.Speaker;
import de.maxhenkel.voicechat.voice.common.SoundPacket;
import dev.magyul.MTWMod;
import dev.magyul.util.ClientUtil;
import dev.magyul.util.ServerUtil;
import dev.magyul.voicechat.AllSoundPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Pair;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;
import java.util.function.Supplier;

@Mixin(AudioChannel.class)
public abstract class AudioChannelMixin {
    @Shadow(remap = false) @Final private UUID uuid;

    @Shadow(remap = false) private Speaker speaker;

    @Shadow(remap = false) @Final private ClientVoicechat client;

    @Shadow(remap = false) protected abstract void appendRecording(Supplier<short[]> stereo);

    @Inject(method = "writeToSpeaker", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
    private void writeToSpeaker(SoundPacket<?> packet, short[] monoData, CallbackInfo ci, float channelVolume, float volume) {
        if (packet instanceof AllSoundPacket) {
            ClientUtil.receivedAllMic.put(packet.getSender(), ((AllSoundPacket) packet).getSenderName());
            var processedMonoData = PluginManager.instance().onReceiveStaticClientSound(this.uuid, monoData);
            speaker.play(processedMonoData, volume, packet.getCategory());
            client.getTalkCache().updateTalking(this.uuid, false);
            appendRecording(() -> PositionalAudioUtils.convertToStereo(processedMonoData));
        }
    }
}
