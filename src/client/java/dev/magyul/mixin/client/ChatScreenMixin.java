package dev.magyul.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    @Unique
    private static final float FADE_TIME = 170;
    @Unique
    private static final float FADE_OFFSET = 8;

    @Unique
    private boolean wasOpenedLastFrame = false;
    @Unique
    private long lastOpenTime = 0;
    @Unique
    private float offsetY = 0;

    @Inject(method = "render", at = @At("HEAD"))
    private void renderPre(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        var client = MinecraftClient.getInstance();
        if (client.player != null) {
            if (!wasOpenedLastFrame && !client.player.isSleeping()) {
                wasOpenedLastFrame = true;
                lastOpenTime = System.currentTimeMillis();
            }
        }

        var screenFactor = (float)client.getWindow().getHeight() / 1080;
        var timeSinceOpen = Math.min((float)(System.currentTimeMillis() - lastOpenTime), FADE_TIME);
        var alpha = 1 - (timeSinceOpen / FADE_TIME);

        var c1 = 1.70158f;
        var c3 = c1 + 1;
        var modifiedAlpha = c3 * alpha * alpha * alpha - c1 * alpha * alpha;

        offsetY = modifiedAlpha * FADE_OFFSET * screenFactor;

        context.getMatrices().translate(0, offsetY, 0);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void renderPost(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        context.getMatrices().translate(0, -offsetY, 0);
    }
}
