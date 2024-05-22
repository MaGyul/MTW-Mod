package dev.magyul.api;

import dev.magyul.util.OverlayHelper;
import net.minecraft.client.gui.DrawContext;

public interface OverlayExtender {
    OverlayHelper.State mtwmod$getState();

    void mtwmod$setState(OverlayHelper.State state);

    default void mtwmod$miniRender(DrawContext context) {
        throw new UnsupportedOperationException("The '" + this.getClass().getCanonicalName() + "' overlay doesn't have a mini-render!");
    }
}
