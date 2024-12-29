package dev.magyul.cocoainput.wrapper;

import dev.magyul.cocoainput.CocoaInput;
import dev.magyul.cocoainput.plugin.IMEOperator;
import dev.magyul.cocoainput.plugin.IMEReceiver;
import dev.magyul.cocoainput.util.Rect;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;

import java.util.Optional;

public class BookEditScreenWrapper extends IMEReceiver {
    private final BookEditScreen owner;

    public BookEditScreenWrapper(BookEditScreen owner) {
        this.owner = owner;
        IMEOperator myIME = CocoaInput.getController().generateIMEOperator(this);
        myIME.setFocused(true);
    }

    @Override
    protected boolean checkInsert(String text) {
        var string = (new StringBuilder(getText())).insert(getCursorPos(), text).toString();
        if (owner.signing) {
            return string.length() < 16;
        } else {
            return string.length() < 1024 && owner.textRenderer.getWrappedLinesHeight(string, 114) <= 128;
        }
    }

    @Override
    protected void setText(String text) {
        if (owner.signing) {
            owner.title = text;
        } else {
            owner.setPageContent(text);
        }
    }

    @Override
    protected String getText() {
        if (owner.signing) {
            return owner.title;
        } else {
            return owner.getCurrentPageContent();
        }
    }

    @Override
    public int getCursorPos() {
        if (owner.signing) {
            return owner.bookTitleSelectionManager.getSelectionStart();
        } else {
            return owner.currentPageSelectionManager.getSelectionStart();
        }
    }

    @Override
    protected void setCursorPos(int pos) {
        if (owner.signing) {
            owner.bookTitleSelectionManager.moveCursorTo(pos, true);
        } else {
            owner.currentPageSelectionManager.moveCursorTo(pos, true);
        }
    }

    @Override
    protected void setSelectionPos(int pos) {
        if (owner.signing) {
            owner.bookTitleSelectionManager.setSelection(pos, pos);
        } else {
            owner.currentPageSelectionManager.setSelection(pos, pos);
        }
    }

    @Override
    public Rect getRect() {
        TextRenderer font = owner.textRenderer;
        if (owner.signing) {
            var title = owner.title;
            if (title.length() >= originalCursorPosition) {
                title = title.substring(0, originalCursorPosition);
            }
            return new Rect(
                    (font.getWidth(title) / 2f + ((owner.width - 192f) / 2f) + 36f + (116f - 0f) / 2f),
                    (50 + font.fontHeight),
                    0, 0
            );
        } else {
            var manager = font.getTextHandler();
            var lines = manager.wrapLines(owner.getCurrentPageContent(), 116, Style.EMPTY);
            final String[] lastLine = new String[1];
            if (lines.isEmpty()) {
                lastLine[0] = "";
            } else {
                StringVisitable.Visitor<?> acceptor = asString -> {
                    lastLine[0] = asString;
                    return Optional.empty();
                };
                lines.get(lines.size() - 1).visit(acceptor);
            }
            return new Rect(
                    (((owner.width - 192f) / 2f) + 36f + font.getWidth(lastLine[0])),
                    (34f + lines.size() * font.fontHeight),
                    0, 0
            );
        }
    }
}
