package dev.magyul.mixin;

import dev.magyul.MTWMod;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow public ServerPlayerEntity player;

    @Inject(method = "executeCommand", at = @At("HEAD"))
    private void executeCommand(String command, CallbackInfo ci) {
        MTWMod.LOGGER.info("{} issued server command /{}", player.getStyledDisplayName().getString(), command);
    }
}
