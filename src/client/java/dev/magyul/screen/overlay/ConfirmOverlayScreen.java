package dev.magyul.screen.overlay;

import com.google.common.collect.Lists;
import dev.magyul.screen.OverlayScreen;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class ConfirmOverlayScreen extends OverlayScreen {
    private static final int TITLE_BOTTOM_MARGIN = 20;
    private final Text message;
    private MultilineText messageSplit;
    protected Text yesText;
    protected Text noText;
    private int buttonEnableTimer;
    protected final BooleanConsumer callback;
    private final List<ButtonWidget> buttons;

    public ConfirmOverlayScreen(BooleanConsumer callback, Text title, Text message) {
        this(callback, title, message, ScreenTexts.YES, ScreenTexts.NO);
    }

    public ConfirmOverlayScreen(BooleanConsumer callback, Text title, Text message, Text yesText, Text noText) {
        super(title);
        this.messageSplit = MultilineText.EMPTY;
        this.buttons = Lists.newArrayList();
        this.callback = callback;
        this.message = message;
        this.yesText = yesText;
        this.noText = noText;
    }

    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences(super.getNarratedTitle(), this.message);
    }

    protected void init() {
        super.init();
        this.messageSplit = MultilineText.create(this.textRenderer, this.message, this.width - 50);
        int i = MathHelper.clamp(this.getMessageY() + this.getMessagesHeight() + 20, this.height / 6 + 96, this.height - 24);
        this.buttons.clear();
        this.addButtons(i);
    }

    protected void addButtons(int y) {
        this.addButton(ButtonWidget.builder(this.yesText, (button) ->
                this.callback.accept(true)).dimensions(this.width / 2 - 155, y, 150, 20).build());
        this.addButton(ButtonWidget.builder(this.noText, (button) ->
                this.callback.accept(false)).dimensions(this.width / 2 - 155 + 160, y, 150, 20).build());
    }

    protected void addButton(ButtonWidget button) {
        this.buttons.add(this.addDrawableChild(button));
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.getTitleY(), 16777215);
        this.messageSplit.drawCenterWithShadow(context, this.width / 2, this.getMessageY());
    }

    private int getTitleY() {
        int i = (this.height - this.getMessagesHeight()) / 2;
        int var10000 = i - 20;
        Objects.requireNonNull(this.textRenderer);
        return MathHelper.clamp(var10000 - 9, 10, 80);
    }

    private int getMessageY() {
        return this.getTitleY() + 20;
    }

    private int getMessagesHeight() {
        int var10000 = this.messageSplit.count();
        Objects.requireNonNull(this.textRenderer);
        return var10000 * 9;
    }

    public void disableButtons(int ticks) {
        this.buttonEnableTimer = ticks;

        ButtonWidget buttonWidget;
        for(Iterator<ButtonWidget> var2 = this.buttons.iterator(); var2.hasNext(); buttonWidget.active = false) {
            buttonWidget = var2.next();
        }

    }

    public void tick() {
        super.tick();
        ButtonWidget buttonWidget;
        if (--this.buttonEnableTimer == 0) {
            for(Iterator<ButtonWidget> var1 = this.buttons.iterator(); var1.hasNext(); buttonWidget.active = true) {
                buttonWidget = var1.next();
            }
        }

    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.callback.accept(false);
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }
}
