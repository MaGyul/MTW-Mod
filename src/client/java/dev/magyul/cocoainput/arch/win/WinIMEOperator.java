package dev.magyul.cocoainput.arch.win;

import dev.magyul.cocoainput.plugin.IMEOperator;
import dev.magyul.cocoainput.plugin.IMEReceiver;
import dev.magyul.cocoainput.util.Logger;

public class WinIMEOperator implements IMEOperator {
    public IMEReceiver owner;
    private boolean focus = false;

    public WinIMEOperator(IMEReceiver owner) {
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

    @Override
    public void setFocused(boolean inFocused) {
        if (inFocused == focus) return;
        focus = inFocused;
        Logger.debug("setFocusedCalled {}", inFocused);
        if (inFocused) {
            WinController.focusedOperator = this;
            Handle.INSTANCE.set_focus(1);
        } else {
            if (WinController.focusedOperator == this) {
                owner.insertText("");
                WinController.focusedOperator = null;
                Handle.INSTANCE.set_focus(0);
            }
        }
    }
}
