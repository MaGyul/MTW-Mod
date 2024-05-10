package dev.magyul.cocoainput.wrapper;

import dev.magyul.cocoainput.plugin.IMEOperator;
import dev.magyul.cocoainput.plugin.IMEReceiver;
import dev.magyul.cocoainput.CocoaInput;
import dev.magyul.util.Rect;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.input.CursorMovement;

public class EditBoxWidgetWrapper extends IMEReceiver {
    private final IMEOperator myIME;
    private final EditBoxWidget owner;

    public EditBoxWidgetWrapper(EditBoxWidget owner) {
        this.owner = owner;
        myIME = CocoaInput.getController().generateIMEOperator(this);
    }

    public void setFocused(boolean newParam) {
        myIME.setFocused(newParam);
    }

    @Override
    protected boolean checkInsert(String text) {
        return false;
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
    protected int getCursorPos() {
        return owner.editBox.getCursor();
    }

    @Override
    protected void setCursorPos(int pos) {
        owner.editBox.moveCursor(CursorMovement.ABSOLUTE, pos);
    }

    @Override
    protected void setSelectionPos(int pos) {
        owner.editBox.selectionEnd = pos;
    }

    @Override
    public Rect getRect() {
        var textRenderer = owner.editBox.textRenderer;
        var text = owner.getText();
        if (text.length() >= originalCursorPosition) {
            text = text.substring(0, originalCursorPosition);
        }
        return new Rect(
                (textRenderer.getWidth(text) + (owner.getX() + 4f)),
                ((owner.getY() - 4f) + (textRenderer.fontHeight * owner.editBox.getCurrentLineIndex())),
                owner.getWidth(),
                owner.getHeight()
        );
    }
}
