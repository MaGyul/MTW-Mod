package dev.magyul.api;

import dev.magyul.util.OverlayStateHelper;
import net.minecraft.client.gui.DrawContext;

public interface OverlayExtender {
    OverlayStateHelper.State mtwmod$getState();

    void mtwmod$setState(OverlayStateHelper.State state);

    default void mtwmod$render(DrawContext context) {
        throw new UnsupportedOperationException("The '" + this.getClass().getCanonicalName() + "' overlay doesn't have a mtwmod$render!");
    }
}
