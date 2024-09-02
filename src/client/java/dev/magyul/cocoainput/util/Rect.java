package dev.magyul.cocoainput.util;

import com.sun.jna.Pointer;
import dev.magyul.cocoainput.CocoaInput;

public record Rect(float x, float y, float width, float height) {
    public static final Rect ZERO = new Rect(0, 0, 0, 0);

    public Pointer writeMemory(Pointer ret, boolean applyScaledFactor) {
        float[] buff = new float[]{x, y, width, height};

        if (applyScaledFactor) {
            float factor = (float) CocoaInput.getScreenScaledFactor();
            buff[0] *= factor;
            buff[1] *= factor;
            buff[2] *= factor;
            buff[3] *= factor;
        }

        ret.write(0, buff, 0, 4);
        return ret;
    }
}