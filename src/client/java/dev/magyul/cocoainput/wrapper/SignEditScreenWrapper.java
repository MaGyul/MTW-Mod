package dev.magyul.cocoainput.wrapper;

import dev.magyul.cocoainput.CocoaInput;
import dev.magyul.cocoainput.plugin.IMEOperator;
import dev.magyul.cocoainput.plugin.IMEReceiver;
import dev.magyul.cocoainput.util.Rect;
import net.minecraft.block.SignBlock;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;

@SuppressWarnings("DataFlowIssue")
public class SignEditScreenWrapper extends IMEReceiver {
    private final AbstractSignEditScreen owner;

    public SignEditScreenWrapper(AbstractSignEditScreen owner) {
        this.owner = owner;
        IMEOperator myIME = CocoaInput.getController().generateIMEOperator(this);
        myIME.setFocused(true);
    }

    @Override
    protected boolean checkInsert(String text) {
        var string = (new StringBuilder(getText())).insert(getCursorPos(), text).toString();
        return owner.textRenderer.getWidth(string) <= owner.blockEntity.getMaxTextWidth();
    }

    @Override
    protected void setText(String text) {
        owner.setCurrentRowMessage(text);
    }

    @Override
    protected String getText() {
        return owner.text.getMessage(owner.currentRow, false).getString();
    }

    @Override
    public int getCursorPos() {
        return owner.selectionManager.getSelectionStart();
    }

    @Override
    protected void setCursorPos(int pos) {
        owner.selectionManager.moveCursorTo(pos, true);
    }

    @Override
    protected void setSelectionPos(int pos) {
        owner.selectionManager.setSelection(pos, pos);
    }

    @Override
    public Rect getRect() {
        float y = 91 + (owner.currentRow - 1) * (10);
        if (!(owner.blockEntity.getCachedState().getBlock() instanceof SignBlock)) {
            y += 30;
        }
        var text = owner.text.getMessage(owner.currentRow, false).toString();
        if (text.length() >= originalCursorPosition) {
            text = text.substring(0, originalCursorPosition);
        }
        return new Rect(owner.width / 2f + owner.textRenderer.getWidth(text) / 2f, y, 0, 0);
    }
}
