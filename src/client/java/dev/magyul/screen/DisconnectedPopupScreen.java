package dev.magyul.screen;

import dev.magyul.MTWMod;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class DisconnectedPopupScreen extends OverlayScreen {
    private static final Identifier BACKGROUND = new Identifier(MTWMod.ID, "background");
    private static final Text CLOSE_TEXT = Text.translatable("disconnected.gui.close");
    private int leftPos;
    private int topPos;
    private int popupWidth;
    private int popupHeight;
    private final Text reason;

    public DisconnectedPopupScreen(Text title, Text reason) {
        super(title);
        this.reason = reason;
    }

    @Override
    protected void init() {
        popupWidth = 400;
        popupHeight = 200;
        leftPos = (width - popupWidth) / 2;
        topPos = (height - popupHeight) / 2;
        addDrawableChild(createTextWidget(title, textRenderer).setTextColor(0));

        var mtwMaxWidth = popupWidth - 50;
        addDrawableChild(new MultilineTextWidget(getCenter(mtwMaxWidth), topPos + 50, reason, textRenderer)
                .setMaxWidth(mtwMaxWidth).setTextColor(0).setCentered(true));
        addDrawableChild(ButtonWidget.builder(CLOSE_TEXT, (button) -> close())
                .position(getCenter(ButtonWidget.DEFAULT_WIDTH),  getBottom(ButtonWidget.DEFAULT_HEIGHT) - 10).build());
    }

    private TextWidget createTextWidget(Text message, TextRenderer textRenderer) {
        int width = textRenderer.getWidth(message.asOrderedText());
        Objects.requireNonNull(textRenderer);
        return new TextWidget(getCenter(width), topPos + 10, width, 9, message, textRenderer);
    }

    private int getCenter(int width) {
        return leftPos;// + ((leftPos / 2) - (width / 2));
    }

    private int getBottom(int height) {
        return (topPos + popupHeight) - height;
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        context.drawGuiTexture(BACKGROUND, leftPos, topPos, popupWidth, popupHeight);
    }

    @Override
    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences(title, reason);
    }
}
