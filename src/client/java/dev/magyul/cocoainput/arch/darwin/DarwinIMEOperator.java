package dev.magyul.cocoainput.arch.darwin;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import dev.magyul.cocoainput.CocoaInput;
import dev.magyul.cocoainput.plugin.IMEOperator;
import dev.magyul.cocoainput.plugin.IMEReceiver;
import dev.magyul.util.Rect;

import java.util.UUID;

public class DarwinIMEOperator implements IMEOperator {
    IMEReceiver owner;
    String uuid;
    CallbackFunction.Func_insertText insertText;
    CallbackFunction.Func_setMarkedText setMarkedText;
    CallbackFunction.Func_firstRectForCharacterRange firstRectForCharacterRange;
    boolean isFocused = false;

    public DarwinIMEOperator(IMEReceiver owner) {
        this.owner = owner;
        uuid = UUID.randomUUID().toString();
        insertText = (str, position, length) -> {
            CocoaInput.LOGGER.debug("Textfield {} received inserted text.", uuid);
            owner.insertText(str);
        };
        setMarkedText = (str, position1, length1, position2, length2) ->
                owner.setMarkedText(str, position1, length1);
        firstRectForCharacterRange = () -> {
            CocoaInput.LOGGER.debug("Called to determine where to draw.");
            Rect point = owner.getRect();
            float[] buff;
            if (point == null) {
                buff = new float[]{0, 0, 0, 0};
            } else {
                buff = new float[]{point.x(), point.y(), point.width(), point.height()};
            }
            float factor = (float) CocoaInput.getScreenScaledFactor();
            buff[0] *= factor;
            buff[1] *= factor;
            buff[2] *= factor;
            buff[3] *= factor;

            Pointer ret = new Memory(Float.BYTES * 4);
            ret.write(0, buff, 0, 4);
            return ret;
        };
        CocoaInput.LOGGER.info("IMEOperator addInstance: {}", uuid);
        Handle.INSTANCE.addInstance(uuid, insertText, setMarkedText, firstRectForCharacterRange);
    }

    @Override
    public void discardMarkedText() {
        Handle.INSTANCE.discardMarkedText(uuid);
    }

    @Override
    public void removeInstance() {
        Handle.INSTANCE.removeInstance(uuid);
    }

    @Override
    public void setFocused(boolean focused) {
        if (focused != isFocused) {
            CocoaInput.LOGGER.info("IMEOperator.setFocused: {}", focused);
            Handle.INSTANCE.setIfReceiveEvent(uuid, focused ? 1 : 0);
            isFocused = focused;
        }
    }
}
