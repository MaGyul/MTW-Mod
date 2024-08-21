package dev.magyul.mixin.client.rrls;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.magyul.MTWMod;
import dev.magyul.util.ClientUtil;
import dev.magyul.util.DummyDrawContext;
import dev.magyul.util.OverlayHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.resource.ResourceReload;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.function.Consumer;

import static net.minecraft.util.math.ColorHelper.Argb;

@Mixin(SplashOverlay.class)
public abstract class SplashOverlayMixin extends Overlay {
    @Unique
    private static final Identifier BACKGROUND = Identifier.of(MTWMod.ID, "background");

    @Shadow @Final private MinecraftClient client;

    @Shadow protected abstract void renderProgressBar(DrawContext drawContext, int minX, int minY, int maxX, int maxY, float opacity);

    @Shadow
    private static int withAlpha(int color, int alpha) {
        return 0;
    }

    @Shadow @Final private boolean reloading;
    @Shadow @Final private ResourceReload reload;
    @Unique
    private long reloadCompleteTime = -1L;
    @Unique
    private long reloadStartTime = -1L;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(MinecraftClient client, ResourceReload monitor, Consumer<Optional<Throwable>> exceptionHandler, boolean reloading, CallbackInfo ci) {
        mtwmod$setState(OverlayHelper.lookupState(reloading));
    }

    @Override
    public void mtwmod$miniRender(DrawContext context) {
        int scaledWidth = context.getScaledWindowWidth();
//        int scaledHeight = context.getScaledWindowHeight();
        long l = Util.getMeasuringTimeMs();
        if (this.reloading && this.reloadStartTime == -1L) {
            this.reloadStartTime = l;
        }

        float f = this.reloadCompleteTime > -1L ? (float)(l - this.reloadCompleteTime) / 1000.0F : -1.0F;
        float g = this.reloadStartTime > -1L ? (float)(l - this.reloadStartTime) / 500.0F : -1.0F;
        var fade = 1.0F - MathHelper.clamp(f - 0.95F, 0.0F, 1.0F);
        var fadeAlpha = MathHelper.ceil(fade * 255F);

        int width = 130;
        int height = 40;
        int x = scaledWidth - width - 5;
        if (ClientUtil.isShowingToast()) {
            x = 5;
        }
        int y = 5;
        RenderSystem.enableBlend();
        RenderSystem.depthMask(false);
        RenderSystem.enableDepthTest();
        context.setShaderColor(1.0F, 1.0F, 1.0F, fadeAlpha);
        context.drawGuiTexture(BACKGROUND, x, y, width, height);
        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        context.drawText(client.textRenderer, Text.translatable("reloading.resource"), x + 10, y + 10, withAlpha(4210752, fadeAlpha), false);
        int barX = (x + (width / 2));
        int barY = (y + (height / 2)) + 8;
        renderProgressBar(context,  barX - 55, barY - 5, barX + 54, barY + 5, fade);
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.disableDepthTest();

        /*
        int s = (int)((double) scaledHeight * 0.8325);
        int r = (int)(Math.min((double) scaledWidth * 0.75, scaledHeight) * 0.5);
        this.renderProgressBar(context, scaledWidth / 2 - r, s - 5, scaledWidth / 2 + r, s + 5, 0.8F);




        a
         */

        if (this.reloadCompleteTime == -1L && this.reload.isComplete() && (!this.reloading || g >= 2.0F)) {
            this.reloadCompleteTime = Util.getMeasuringTimeMs();
        }
    }

    @Redirect(method = "renderProgressBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/ColorHelper$Argb;getArgb(IIII)I"))
    private int progressBarColor(int alpha, int red, int green, int blue) {
        if (OverlayHelper.isRenderingState(this)) {
            return Argb.getArgb(alpha, 0, 0, 0);
        } else {
            return Argb.getArgb(alpha, red, green, blue);
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (mtwmod$getState() != OverlayHelper.State.DEFAULT) {
            mtwmod$setState(OverlayHelper.lookupState(mtwmod$getState() != OverlayHelper.State.WAIT));
        }
    }

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;render(Lnet/minecraft/client/gui/DrawContext;IIF)V"))
    private boolean screenRender(Screen instance, DrawContext context, int mouseX, int mouseY, float delta) {
        return !(context instanceof DummyDrawContext);
    }

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setOverlay(Lnet/minecraft/client/gui/screen/Overlay;)V"))
    private boolean infinityLoading(MinecraftClient instance, Overlay overlay) {
        return true;
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_clear(IZ)V", remap = false))
    private void _clear(int mask, boolean getError, Operation<Void> original) {
        if (!mtwmod$getState().isRendering()) {
            original.call(mask, getError);
        }
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_clearColor(FFFF)V", remap = false))
    private void _clearColor(float red, float green, float blue, float alpha, Operation<Void> original) {
        if (!mtwmod$getState().isRendering()) {
            original.call(red, green, blue, alpha);
        }
    }

    @WrapOperation(method = "renderProgressBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/ColorHelper$Argb;getArgb(IIII)I"))
    private int rainbowProgress(int alpha, int red, int green, int blue, Operation<Integer> original) {
//        return false && mtwmod$getState() != OverlayHelper.State.DEFAULT ? withAlpha(ThreadLocalRandom.current().nextInt(0, 16777215), alpha) :
        return original.call(alpha, red, green, blue);
    }

    @ModifyConstant(method = "render", constant = @Constant(floatValue = 1000.0F, ordinal = 0), require = 0)
    private float changeAnimationSpeed(float constant) {
        return 1000.0F;
    }

    @Override
    public boolean pausesGame() {
        return super.pausesGame();
    }
}
