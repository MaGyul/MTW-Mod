package dev.magyul.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.magyul.MTWMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
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
            TEXTURE = new Identifier(MTWMod.ID, "toast/advancement");
        }
    }

    @Mixin(RecipeToast.class)
    public static class Recipe {
        @Mutable
        @Shadow @Final private static Identifier TEXTURE;

        @Inject(method = "<clinit>", at = @At("TAIL"))
        private static void injected(CallbackInfo cb) {
            TEXTURE = new Identifier(MTWMod.ID, "toast/recipe");
        }
    }

    @Mixin(SystemToast.class)
    public static class System {
        @Mutable
        @Shadow @Final private static Identifier TEXTURE;

        @Inject(method = "<clinit>", at = @At("TAIL"))
        private static void injected(CallbackInfo cb) {
            TEXTURE = new Identifier(MTWMod.ID, "toast/system");
        }
    }

    @Mixin(TutorialToast.class)
    public static class Tutorial {
        @Mutable
        @Shadow @Final private static Identifier TEXTURE;

        @Inject(method = "<clinit>", at = @At("TAIL"))
        private static void injected(CallbackInfo cb) {
            TEXTURE = new Identifier(MTWMod.ID, "toast/tutorial");
        }
    }

    @Mixin(ToastManager.Entry.class)
    public static abstract class Entry<T extends Toast> {
        @Shadow private long startTime;

        @Shadow private Toast.Visibility visibility;

        @Shadow private long showTime;

        @Shadow @Final private T instance;

        @Shadow protected abstract float getDisappearProgress(long time);

        @Shadow @Final int topIndex;

        @Inject(method = "draw", at = @At("HEAD"), cancellable = true)
        private void draw(int x, DrawContext context, CallbackInfoReturnable<Boolean> cb) {
            var client = MinecraftClient.getInstance();
            long i = Util.getMeasuringTimeMs();
            if (startTime == -1L) {
                startTime = i;
                this.visibility.playSound(client.getSoundManager());
            }

            if (this.visibility == Toast.Visibility.SHOW && i - startTime <= 600L) {
                showTime = i;
            }

            context.getMatrices().push();
            context.getMatrices().translate((float) x - (float) instance.getWidth() * getDisappearProgress(i), (float) (topIndex * 32), 800.0F);
            RenderSystem.enableBlend();
            Toast.Visibility visibility = instance.draw(context, client.getToastManager(), i - showTime);
            RenderSystem.disableBlend();
            context.getMatrices().pop();
            if (visibility != this.visibility) {
                startTime = i - (long)((int)((1.0F - getDisappearProgress(i)) * 600.0F));
                this.visibility = visibility;
                this.visibility.playSound(client.getSoundManager());
            }

            cb.setReturnValue(this.visibility == Toast.Visibility.HIDE && i - startTime > 600L);
        }
    }
}
