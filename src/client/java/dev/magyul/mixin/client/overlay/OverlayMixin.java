package dev.magyul.mixin.client.overlay;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.magyul.api.OverlayExtender;
import dev.magyul.util.OverlayStateHelper;
import net.minecraft.client.gui.screen.Overlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Overlay.class)
public class OverlayMixin implements OverlayExtender {
    @Unique
    public OverlayStateHelper.State mtwmod$state;

    public OverlayMixin() {
        this.mtwmod$state = OverlayStateHelper.State.DEFAULT;
    }

    @Override
    public OverlayStateHelper.State mtwmod$getState() {
        return mtwmod$state;
    }

    @Override
    public void mtwmod$setState(OverlayStateHelper.State state) {
        mtwmod$state = state;
    }

    @ModifyReturnValue(method = "pausesGame", at = @At("RETURN"))
    private boolean pausesGame(boolean original) {
        return !mtwmod$getState().isRendering() && original;
    }
}
