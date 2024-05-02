package dev.magyul.mixin.client;

import dev.magyul.MTWModClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.navigation.GuiNavigationType;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    @Shadow @Final private MinecraftClient client;
    @Shadow private double x;
    @Shadow private double y;

    @Inject(method = "onMouseButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getOverlay()Lnet/minecraft/client/gui/screen/Overlay;", ordinal = 0, shift = At.Shift.BEFORE), cancellable = true)
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (MTWModClient.checkOverlayUsed()) {
            this.client.setNavigationType(GuiNavigationType.MOUSE);

            if (this.client.getOverlay() == null) {
                double d = this.x * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth();
                double e = this.y * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight();
                Screen screen = MTWModClient.instance.overlayScreen;
                if (action == 1) {
                    screen.applyMousePressScrollNarratorDelay();
                    Screen.wrapScreenError(() -> screen.mouseClicked(d, e, button),
                            "mouseClicked event handler", screen.getClass().getCanonicalName());
                } else {
                    Screen.wrapScreenError(() -> screen.mouseReleased(d, e, button),
                            "mouseReleased event handler", screen.getClass().getCanonicalName());
                }
            }

            ci.cancel();
        }
    }

    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (window == client.getWindow().getHandle() && MTWModClient.checkOverlayUsed()) {
            boolean bl = this.client.options.getDiscreteMouseScroll().getValue();
            double d = this.client.options.getMouseWheelSensitivity().getValue();
            double e = (bl ? Math.signum(horizontal) : horizontal) * d;
            double f = (bl ? Math.signum(vertical) : vertical) * d;
            if (this.client.getOverlay() == null) {
                double g = this.x * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth();
                double h = this.y * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight();
                MTWModClient.instance.overlayScreen.mouseScrolled(g, h, e, f);
                MTWModClient.instance.overlayScreen.applyMousePressScrollNarratorDelay();
                ci.cancel();
            }
        }
    }

    @ModifyVariable(method = "onCursorPos", at = @At("STORE"), ordinal = 0)
    private Screen modifyCursorPos(Screen screen) {
        if (MTWModClient.checkOverlayUsed()) {
            return MTWModClient.instance.overlayScreen;
        }
        return screen;
    }

    /*
    @Inject(method = "onCursorPos", at = @At("HEAD"), cancellable = true)
    private void onCursorPos(long window, double x, double y, CallbackInfo ci) {
        if (window == client.getWindow().getHandle() && MTWModClient.instance != null) {
            var screen = MTWModClient.instance.overlayScreen;
            if (screen != null && client.getOverlay() == null) {
                double cx = x * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth();
                double cy = y * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight();
                Screen.wrapScreenError(() ->
                        screen.mouseMoved(cx, cy),
                        "mouseMoved event handler", screen.getClass().getCanonicalName());
                if (this.activeButton != -1 && this.glfwTime > 0.0) {
                    double dx = (x - this.x) * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth();
                    double dy = (y - this.y) * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight();
                    Screen.wrapScreenError(() ->
                            screen.mouseDragged(cx, cy, this.activeButton, dx, dy),
                            "mouseDragged event handler", screen.getClass().getCanonicalName());
                }

                screen.applyMouseMoveNarratorDelay();
                ci.cancel();
            }
        }
    }
     */
}
