package dev.magyul.mixin;

import com.mojang.authlib.GameProfile;
import dev.magyul.MTWMod;
import dev.magyul.util.ServerUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;getMeasuringTimeMs()J", ordinal = 0))
    private void runServer(CallbackInfo cb) {
        ServerUtil.allowLogins.set(true);
        MTWMod.LOGGER.info("The server is started");
    }

    @Redirect(method = "createMetadataPlayers", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getGameProfile()Lcom/mojang/authlib/GameProfile;"))
    private GameProfile getGamaProfile(ServerPlayerEntity player) {
        var strip = Formatting.strip(player.getName().getString());
        if (strip == null) strip = player.getName().getString();
        return new GameProfile(player.getUuid(), strip);
    }

    @Inject(method = "shutdown", at = @At("HEAD"))
    private void stoppingServer(CallbackInfo cb) {
        ServerUtil.allowLogins.set(false);
    }
}
