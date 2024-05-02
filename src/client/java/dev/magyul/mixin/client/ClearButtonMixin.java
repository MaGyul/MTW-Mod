package dev.magyul.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PressableWidget.class)
public abstract class ClearButtonMixin extends ClickableWidget {

    @Shadow public abstract void drawMessage(DrawContext context, TextRenderer textRenderer, int color);

    private ClearButtonMixin() {
        super(0, 0, 0, 0, Text.empty());
    }

    @Inject(method = "renderWidget", at = @At("HEAD"), cancellable = true)
    private void renderWidget(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo cb) {
        cb.cancel();
        MinecraftClient minecraft = MinecraftClient.getInstance();
        context.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        context.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), getBackColor());
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        drawMessage(context, minecraft.textRenderer, getTextColor() | MathHelper.ceil(alpha * 255.0F) << 24);
    }

    @Unique
    private int getBackColor() {
        if (!this.active) {
            return -1275068416;
        } else if (this.isSelected()) {
            return -2113929216;
        }
        return 1677721600;
    }

    @Unique
    private int getTextColor() {
        if (!active) {
            return 10526880;
        } else if (this.isSelected()) {
            return 16777120;
        }
        return 16777215;
    }
}
