package dev.magyul.util;

import net.minecraft.client.gui.screen.Overlay;

public class OverlayStateHelper {

    public static OverlayStateHelper.State getState(boolean reloading) {
        if (!reloading) return State.DEFAULT;
        return State.HIDE;
    }

    public static boolean isRendering(Overlay overlay) {
        return overlay != null && overlay.mtwmod$getState().isRendering();
    }

    public enum State {
        DEFAULT(false),
        HIDE(true);

        private final boolean render;

        State(boolean r) {
            this.render = r;
        }

        public boolean isRendering() {
            return this.render;
        }
    }
}
