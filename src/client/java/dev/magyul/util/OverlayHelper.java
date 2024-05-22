package dev.magyul.util;

import net.minecraft.client.gui.screen.Overlay;

public class OverlayHelper {

    public static OverlayHelper.State lookupState(boolean reloading) {
        if (!reloading) return State.DEFAULT;
        return State.HIDE;
    }

    public static boolean isRenderingState(Overlay overlay) {
        return overlay != null && overlay.mtwmod$getState().isRendering();
    }

    public enum State {
        DEFAULT(false),
        HIDE(true),
        WAIT(false);

        private final boolean render;

        State(boolean r) {
            this.render = r;
        }

        public boolean isRendering() {
            return this.render;
        }
    }
}
