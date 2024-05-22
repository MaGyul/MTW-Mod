package dev.magyul.mixin.client.rrls;

import com.llamalad7.mixinextras.sugar.Local;
import dev.magyul.MTWMod;
import dev.magyul.util.DummyDrawContext;
import dev.magyul.util.OverlayHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow @Final MinecraftClient client;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V", ordinal = 1))
    private void dummyRender(float tickDelta, long startTime, boolean tick, CallbackInfo ci, @Local(ordinal = 0) DrawContext context) {
//        if (OverlayHelper.isRenderingState(client.overlay)) {
//            context.draw(() -> {
//                var textRenderer = client.textRenderer;
//                var width = textRenderer.getWidth("Hello World!");
//                context.drawText(textRenderer, "Hello World!", context.getScaledWindowWidth() - width, 0, 4210752, false);
//            });
//        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;draw()V"))
    public void miniRender(float partialTicks, long nanoTime, boolean renderLevel, CallbackInfo ci, @Local(ordinal = 0) DrawContext context) {
        try {
            Overlay overlay = this.client.overlay;
            if (OverlayHelper.isRenderingState(overlay)) {
                overlay.render(DummyDrawContext.INSTANCE, 0, 0, this.client.getLastFrameDuration());
                this.client.getProfiler().push("overlay");
                context.getMatrices().push();
                context.getMatrices().translate(0, 0, 0);
                overlay.mtwmod$miniRender(context);
                context.getMatrices().pop();
                this.client.getProfiler().pop();
            }
        } catch (RuntimeException ex) {
            MTWMod.LOGGER.error("render error", ex);
        }

    }
}
