package dev.magyul.mixin.client;

import dev.magyul.MTWModClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilderStorage;
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

    @Shadow @Final private BufferBuilderStorage buffers;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V", ordinal = 1, shift = At.Shift.BEFORE))
    private void render(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        //boolean ticks, float f, boolean bl, int i, int j, Window window, Matrix4f matrix4f, MatrixStack matrixStack, DrawContext context
//        if (client.isFinishedLoading() && MTWModClient.instance.overlayScreen != null) {
//            int x = (int)(this.client.mouse.getX() * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth());
//            int y = (int)(this.client.mouse.getY() * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight());
//            DrawContext context = new DrawContext(this.client, this.buffers.getEntityVertexConsumers());
//            MTWModClient.instance.overlayScreen.renderWithTooltip(context, x, y, this.client.getLastFrameDuration());
//            context.draw();
//        }
    }
}
