package dev.magyul.mixin;

import dev.magyul.MTWMod;
import dev.magyul.util.ServerUtil;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;getMeasuringTimeNano()J", ordinal = 0, shift = At.Shift.BEFORE))
    private void runServer(CallbackInfo cb) {
        ServerUtil.allowLogins.set(true);
        MTWMod.LOGGER.info("The server is started");
    }

    @Inject(method = "shutdown", at = @At("HEAD"))
    private void stoppingServer(CallbackInfo cb) {
        ServerUtil.allowLogins.set(false);
    }
}
