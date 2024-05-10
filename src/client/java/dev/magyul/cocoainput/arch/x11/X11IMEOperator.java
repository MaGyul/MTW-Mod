package dev.magyul.cocoainput.arch.x11;

import dev.magyul.cocoainput.plugin.IMEOperator;
import dev.magyul.cocoainput.plugin.IMEReceiver;

public class X11IMEOperator implements IMEOperator {
    public IMEReceiver owner;
    private boolean focus = false;

    public X11IMEOperator(IMEReceiver owner) {
        this.owner = owner;
    }

    @Override
    public void discardMarkedText() {
        // TODO Auto-generated method stub
    }

    @Override
    public void removeInstance() {
        // TODO Auto-generated method stub
    }

    public void setFocused(boolean focus) {
        if (focus == this.focus) return;
        this.focus = focus;
        Logger.debug("setFocusedCalled {}", focus);
        if (focus) {
            X11Controller.focusedOperator = this;
            Handle.INSTANCE.set_focus(1);
        } else {
            if (X11Controller.focusedOperator == this) {
                owner.insertText("");
                X11Controller.focusedOperator = null;
                Handle.INSTANCE.set_focus(0);
                X11Controller.setupKeyboardEvent();
            }
        }
    }
}
