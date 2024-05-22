package dev.magyul.mixin.client.rrls;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.magyul.util.OverlayHelper;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({GameRenderer.class, Keyboard.class, Mouse.class})
public class RendererKeyboardMouseMixin {
    @WrapOperation(
            method = {"*"},
            at = {@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/MinecraftClient;getOverlay()Lnet/minecraft/client/gui/screen/Overlay;"
            )}
    )
    public Overlay rrls$miniRender(MinecraftClient instance, Operation<Overlay> original) {
        Overlay overlay = original.call(instance);
        return OverlayHelper.isRenderingState(overlay) ? null : overlay;
    }
}
