package dev.magyul.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.magyul.MTWMod;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.*;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class ToastMixin {

    @Mixin(AdvancementToast.class)
    public static class Advancement {
        @Mutable
        @Shadow @Final private static Identifier TEXTURE;

        @Inject(method = "<clinit>", at = @At("TAIL"))
        private static void injected(CallbackInfo cb) {
            TEXTURE = Identifier.of(MTWMod.ID, "toast/advancement");
        }
    }

    @Mixin(RecipeToast.class)
    public static class Recipe {
        @Mutable
        @Shadow @Final private static Identifier TEXTURE;

        @Inject(method = "<clinit>", at = @At("TAIL"))
        private static void injected(CallbackInfo cb) {
            TEXTURE = Identifier.of(MTWMod.ID, "toast/recipe");
        }
    }

    @Mixin(SystemToast.class)
    public static class System {
        @Mutable
        @Shadow @Final private static Identifier TEXTURE;

        @Inject(method = "<clinit>", at = @At("TAIL"))
        private static void injected(CallbackInfo cb) {
            TEXTURE = Identifier.of(MTWMod.ID, "toast/system");
        }
    }

    @Mixin(TutorialToast.class)
    public static class Tutorial {
        @Mutable
        @Shadow @Final private static Identifier TEXTURE;

        @Inject(method = "<clinit>", at = @At("TAIL"))
        private static void injected(CallbackInfo cb) {
            TEXTURE = Identifier.of(MTWMod.ID, "toast/tutorial");
        }
    }

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
