package dev.magyul.mixin;

import dev.magyul.console.MTWConsole;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftDedicatedServer.class)
public class MinecraftDedicatedServerMixin {

    @Inject(method = "setupServer", at = @At(value = "INVOKE", target = "Ljava/lang/Thread;setDaemon(Z)V", ordinal = 0, shift = At.Shift.BEFORE))
    private void setupServer(CallbackInfoReturnable<Boolean> cb) {
    }

    @Mixin(targets = "net.minecraft.server.dedicated.MinecraftDedicatedServer$1")
    public static class MinecraftDedicatedServerThreadMixin {
        @Shadow @Final MinecraftDedicatedServer field_13822;

        @Inject(method = "run", at = @At("HEAD"), cancellable = true)
        private void initTerminal(CallbackInfo cb) {
            cb.cancel();
            new MTWConsole(field_13822).start();
        }
    }
}
