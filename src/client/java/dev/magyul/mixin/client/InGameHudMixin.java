package dev.magyul.mixin.client;

import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Unique
    private int scaledWidth;

    @Shadow public abstract TextRenderer getTextRenderer();

    @Unique
    private MultilineText overlayMessageText;

    @Inject(method = "setOverlayMessage", at = @At("HEAD"))
    private void setOverlayMessage(Text message, boolean tinted, CallbackInfo cb) {
        overlayMessageText = MultilineText.create(getTextRenderer(), message, scaledWidth - 50);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void render(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        scaledWidth = context.getScaledWindowWidth();
    }

    @Redirect(method = "renderOverlayMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithBackground(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIII)I"))
    private int renderRedirect(DrawContext instance, TextRenderer textRenderer, Text text, int x, int y, int width, int color) {
        int linesCount = overlayMessageText.count();
        if (linesCount > 1) {
            if (linesCount % 2 == 1) {
                linesCount--;
            }
            y -= (linesCount * textRenderer.fontHeight) / 2;
        }
        overlayMessageText.drawCenterWithShadow(instance, 0, y, textRenderer.fontHeight, color);
        return 0;
    }
}
