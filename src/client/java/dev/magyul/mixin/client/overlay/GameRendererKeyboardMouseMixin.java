package dev.magyul.mixin.client.overlay;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.magyul.util.OverlayStateHelper;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({GameRenderer.class, Keyboard.class, Mouse.class})
public class GameRendererKeyboardMouseMixin {
    @WrapOperation(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getOverlay()Lnet/minecraft/client/gui/screen/Overlay;"))
    public Overlay mtwmod$render(MinecraftClient instance, Operation<Overlay> original) {
        Overlay overlay = original.call(instance);
        return OverlayStateHelper.isRendering(overlay) ? null : overlay;
    }
}
