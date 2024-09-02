package dev.magyul.mixin.client.voicechat;

import com.llamalad7.mixinextras.sugar.Local;
import de.maxhenkel.voicechat.plugins.PluginManager;
import de.maxhenkel.voicechat.voice.client.AudioChannel;
import de.maxhenkel.voicechat.voice.client.ClientVoicechat;
import de.maxhenkel.voicechat.voice.client.PositionalAudioUtils;
import de.maxhenkel.voicechat.voice.client.speaker.Speaker;
import de.maxhenkel.voicechat.voice.common.SoundPacket;
import dev.magyul.util.ClientUtil;
import dev.magyul.voicechat.AllSoundPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;
import java.util.function.Supplier;

@Mixin(AudioChannel.class)
public abstract class AudioChannelMixin {
    @Shadow(remap = false) @Final private UUID uuid;

    @Shadow(remap = false) private Speaker speaker;

    @Shadow(remap = false) @Final private ClientVoicechat client;

    @Shadow(remap = false) protected abstract void appendRecording(Supplier<short[]> stereo);

    @Inject(method = "writeToSpeaker", at = @At("TAIL"), remap = false)
    private void writeToSpeaker(SoundPacket<?> packet, short[] monoData, CallbackInfo ci, @Local(ordinal = 1) float volume) {
        if (packet instanceof AllSoundPacket) {
            ClientUtil.receivedAllMic.put(packet.getSender(), ((AllSoundPacket) packet).getSenderName());
            var processedMonoData = PluginManager.instance().onReceiveStaticClientSound(this.uuid, monoData);
            speaker.play(processedMonoData, volume, packet.getCategory());
            client.getTalkCache().updateTalking(this.uuid, false);
            appendRecording(() -> PositionalAudioUtils.convertToStereo(processedMonoData));
        }
    }
}
