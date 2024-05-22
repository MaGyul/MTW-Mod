package dev.magyul.mixin.client.rrls;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.magyul.api.OverlayExtender;
import dev.magyul.util.OverlayHelper;
import net.minecraft.client.gui.screen.Overlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Overlay.class)
public class OverlayMixin implements OverlayExtender {
    @Unique
    public OverlayHelper.State mtwmod$state;

    public OverlayMixin() {
        this.mtwmod$state = OverlayHelper.State.DEFAULT;
    }

    @Override
    public OverlayHelper.State mtwmod$getState() {
        return mtwmod$state;
    }

    @Override
    public void mtwmod$setState(OverlayHelper.State state) {
        mtwmod$state = state;
    }

    @ModifyReturnValue(method = "pausesGame", at = @At("RETURN"))
    private boolean pausesGame(boolean original) {
        return !mtwmod$getState().isRendering() && original;
    }
}
