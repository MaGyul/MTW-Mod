package dev.magyul.mixin.voicechat;

import de.maxhenkel.voicechat.voice.common.MicPacket;
import de.maxhenkel.voicechat.voice.common.PlayerState;
import de.maxhenkel.voicechat.voice.common.SoundPacket;
import de.maxhenkel.voicechat.voice.server.ClientConnection;
import de.maxhenkel.voicechat.voice.server.PlayerStateManager;
import de.maxhenkel.voicechat.voice.server.Server;
import dev.magyul.data.PlayerData;
import dev.magyul.voicechat.AllSoundPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(Server.class)
public abstract class ServerMixin {
    @Shadow(remap = false) @Final private PlayerStateManager playerStateManager;

    @Shadow(remap = false) @Nullable public abstract ClientConnection getConnection(UUID playerID);

    @Shadow public abstract void sendSoundPacket(ServerPlayerEntity sender, PlayerState senderState, ServerPlayerEntity receiver, PlayerState receiverState, ClientConnection connection, SoundPacket<?> soundPacket, String source) throws Exception;

    @Inject(method = "processMicPacket", at = @At("HEAD"), cancellable = true)
    private void processMicPacket(ServerPlayerEntity player, PlayerState state, MicPacket packet, CallbackInfo cb) throws Exception {
        var server = player.server;
        if (PlayerData.isAllMic(player)) {
            cb.cancel();
            for (var target : server.getPlayerManager().getPlayerList()) {
                if (player != target) {
                    var receiverState = playerStateManager.getState(target.getUuid());
                    if (receiverState == null) continue;

                    var soundPacket = new AllSoundPacket(player.getUuid(), packet.getData(), packet.getSequenceNumber(), player.getName().getString());
                    var connection = getConnection(receiverState.getUuid());
                    sendSoundPacket(player, state, target, receiverState, connection, soundPacket, "all");
                }
            }
        }
    }
}
