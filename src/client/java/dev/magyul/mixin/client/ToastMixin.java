package dev.magyul.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class ToastMixin {

    @Mixin(ToastManager.Entry.class)
    public static abstract class Entry {
        @Inject(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V", shift = At.Shift.AFTER))
        private void draw$drawPre(int x, DrawContext context, CallbackInfoReturnable<Boolean> cir) {
            RenderSystem.enableBlend();
        }

        @Inject(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/toast/Toast;draw(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/toast/ToastManager;J)Lnet/minecraft/client/toast/Toast$Visibility;", shift = At.Shift.AFTER))
        private void draw$drawPost(int x, DrawContext context, CallbackInfoReturnable<Boolean> cir) {
            RenderSystem.disableBlend();
        }
    }
}
