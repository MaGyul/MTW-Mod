package dev.magyul.cocoainput.arch.darwin;

import com.sun.jna.Memory;
import dev.magyul.cocoainput.CocoaInput;
import dev.magyul.cocoainput.plugin.IMEOperator;
import dev.magyul.cocoainput.plugin.IMEReceiver;
import dev.magyul.cocoainput.util.Rect;

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
            if (point == null) {
                point = Rect.ZERO;
            }
            return point.writeMemory(new Memory(Float.BYTES * 4), true);
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
