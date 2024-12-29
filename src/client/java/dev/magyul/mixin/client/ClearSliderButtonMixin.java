package dev.magyul.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SliderWidget.class)
public abstract class ClearSliderButtonMixin extends ClickableWidget {

    @Shadow protected double value;

    @Shadow protected abstract int getTextureV();

    @Shadow @Final private static Identifier TEXTURE;

    private ClearSliderButtonMixin() {
        super(0, 0, 0, 0, Text.empty());
    }

    @Inject(method = "renderButton", at = @At("HEAD"), cancellable = true)
    private void renderWidget(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo cb) {
        cb.cancel();
        MinecraftClient minecraft = MinecraftClient.getInstance();
        context.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        int color = getBackColor();
        context.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), color);
        context.drawNineSlicedTexture(TEXTURE, this.getX() + (int)(this.value * (double)(this.width - 8)), this.getY(), 8, 20, 20, 4, 200, 20, 0, this.getTextureV());
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        color = getTextColor();
        drawScrollableText(context, minecraft.textRenderer, 2, color | MathHelper.ceil(alpha * 255.0F) << 24);
    }

    @Unique
    private int getBackColor() {
        if (!active) {
            return -1275068416;
        } else if (this.isSelected()) {
            return -2113929216;
        } else {
            return 1677721600;
        }
    }

    @Unique
    private int getTextColor() {
        if (!active) {
            return 10526880;
        } else if (this.isSelected()) {
            return 16777120;
        } else {
            return 16777215;
        }
    }
}
