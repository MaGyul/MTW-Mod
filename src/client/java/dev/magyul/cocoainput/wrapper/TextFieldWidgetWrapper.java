package dev.magyul.cocoainput.wrapper;

import dev.magyul.cocoainput.plugin.IMEOperator;
import dev.magyul.cocoainput.plugin.IMEReceiver;
import dev.magyul.cocoainput.CocoaInput;
import dev.magyul.util.Rect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class TextFieldWidgetWrapper extends IMEReceiver {
    private final IMEOperator myIME;
    private final TextFieldWidget owner;

    public TextFieldWidgetWrapper(TextFieldWidget owner) {
        this.owner = owner;
        myIME = CocoaInput.getController().generateIMEOperator(this);
    }

    public void setFocusUnlocked(boolean newParam) {
        if (!newParam) setFocused(true);
    }

    public void setFocused(boolean newParam) {
        myIME.setFocused(newParam);
    }

    @Override
    protected boolean checkInsert(String text) {
        int i = Math.min(owner.selectionStart, owner.selectionEnd);
        int j = Math.max(owner.selectionStart, owner.selectionEnd);
        int k = owner.maxLength - owner.text.length() - (i - j);
        return owner.editable && k > 0;
    }

    @Override
    protected void setText(String text) {
        owner.setText(text);
    }

    @Override
    protected String getText() {
        return owner.getText();
    }

    @Override
    public int getCursorPos() {
        return owner.getCursor();
    }

    @Override
    protected void setCursorPos(int pos) {
        owner.setCursor(pos, Screen.hasShiftDown());
    }

    @Override
    protected void setSelectionPos(int pos) {
        owner.setSelectionEnd(pos);
    }

    @Override
    public Rect getRect() {
        var textRenderer = owner.textRenderer;
        var text = owner.getText();
        if (text.length() >= originalCursorPosition) {
            text = text.substring(0, originalCursorPosition);
        }
        return new Rect(
                (textRenderer.getWidth(text) + (owner.drawsBackground() ? owner.getX() + 4f : owner.getX())),
                (textRenderer.fontHeight + (owner.drawsBackground() ? owner.getY() + (owner.getHeight() - 8f) / 2f : owner.getY())),
                owner.getWidth(),
                owner.getHeight()
        );
    }
}
