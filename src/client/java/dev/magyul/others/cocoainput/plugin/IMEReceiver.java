package dev.magyul.others.cocoainput.plugin;

import dev.magyul.others.cocoainput.CocoaInput;
import dev.magyul.util.Rect;
import net.minecraft.client.MinecraftClient;

public abstract class IMEReceiver {
    public int length = 0;
    public boolean cursorVisible = true;
    public boolean preeditBegin = false;
    public int markedPos = 1;
    protected int originalCursorPosition = 0;

    public void insertText(String aString) {
        CocoaInput.LOGGER.debug("just comming:({}) now:({})", aString, getText());
        if (!preeditBegin) {
            originalCursorPosition = getCursorPos();
        }
        preeditBegin = false;
        cursorVisible = true;
        setText(new StringBuffer(getText())
                .replace(originalCursorPosition, originalCursorPosition + this.length, "").toString());
        this.length = 0;
        this.markedPos = 0;
        setCursorPos(originalCursorPosition);
        setSelectionPos(originalCursorPosition);
        insertTextNative(aString);
    }

    public void setMarkedText(String aString, int pos, int length) {
//        CocoaInput.LOGGER.info("setMarkedText: {} {} {} {}", aString, pos, length, preeditBegin);
        if (!preeditBegin) {
            originalCursorPosition = getCursorPos();
            preeditBegin = true;
        }
        cursorVisible = false;
        setText(new StringBuffer(getText())
                .replace(originalCursorPosition, originalCursorPosition + this.length, aString).toString());
        this.length = aString.length();
        this.markedPos = pos;
        setCursorPos(originalCursorPosition);
        setSelectionPos(originalCursorPosition);
    }


    public abstract Rect getRect();
    abstract protected boolean checkInsert(String text);
    abstract protected void setText(String text);
    abstract protected String getText();
    abstract protected int getCursorPos();
    abstract protected void setCursorPos(int pos);
    abstract protected void setSelectionPos(int pos);

    protected void insertTextNative(String text)  {
        for (char c : text.trim().toCharArray()) {
            MinecraftClient client = MinecraftClient.getInstance();
            client.keyboard.onChar(client.getWindow().getHandle(), c, 0);
        }
    }
}
