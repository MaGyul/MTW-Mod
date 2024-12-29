package dev.magyul.cmdkeybind;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.suggestion.Suggestion;
import dev.magyul.mixin.client.accessor.ChatInputSuggestorAccessor;
import net.kyrptonaught.kyrptconfig.config.screen.items.ConfigItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.math.Vec2f;

import java.util.Iterator;

public class CommandTextItem extends ConfigItem<String> {
    TextFieldWidget valueEntry;
    ChatInputSuggestor commandSuggestor;
    int lastX;

    public CommandTextItem(Text name, String value, String defaultValue) {
        super(name, value, defaultValue);
        this.useDefaultResetBTN();
        var client = MinecraftClient.getInstance();
        this.valueEntry = new TextFieldWidget(client.textRenderer, 0, 0, 96, 18, Text.literal("Text Entry")) {
            @Override
            protected MutableText getNarrationMessage() {
                return super.getNarrationMessage().append(commandSuggestor.getNarration());
            }
        };
        this.setMaxLength(256);
        this.valueEntry.setText(value);
        this.valueEntry.setChangedListener(this::setValue);
        this.commandSuggestor = new ChatInputSuggestor(client, client.currentScreen, valueEntry, client.textRenderer, false, true, 1, 6, false, Integer.MIN_VALUE);
        if (client.player != null) {
            this.commandSuggestor.refresh();
        }
    }

    public CommandTextItem setMaxLength(int length) {
        this.valueEntry.setMaxLength(length);
        return this;
    }

    public void setValue(String value) {
        super.setValue(value);
        if (MinecraftClient.getInstance().player != null) {
            this.commandSuggestor.refresh();
        }
    }

    public void resetToDefault() {
        this.setValue(this.defaultValue);
        this.valueEntry.setText(this.value);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.commandSuggestor.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return this.valueEntry.keyPressed(keyCode, scanCode, modifiers);
    }

    public boolean charTyped(char chr, int modifiers) {
        return this.valueEntry.charTyped(chr, modifiers);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return this.commandSuggestor.mouseScrolled(amount);
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (commandSuggestor.mouseClicked(mouseX, mouseY, button)) {
            return;
        }
        super.mouseClicked(mouseX, mouseY, button);
        this.valueEntry.setFocused(this.valueEntry.mouseClicked(mouseX, mouseY, button));
    }

    public void render(DrawContext context, int x, int y, int mouseX, int mouseY, float delta) {
        super.render(context, x, y, mouseX, mouseY, delta);
        if (this.valueEntry.isFocused()) {
            this.valueEntry.setWidth(150);
        } else {
            this.valueEntry.setWidth(96);
        }
        this.commandSuggestor.setWindowActive(this.valueEntry.isFocused());

        this.valueEntry.setY(y + 1);
        this.valueEntry.setX(this.resetButton.getX() - this.valueEntry.getWidth() - 7);
        this.valueEntry.render(context, mouseX, mouseY, delta);
        if (lastX != this.valueEntry.getX()) {
            lastX = this.valueEntry.getX();
            if (MinecraftClient.getInstance().player != null) {
                this.commandSuggestor.refresh();
            }
        }

        if (this.valueEntry.isFocused()) {
            renderCommandSuggestor(context, valueEntry.getX(), valueEntry.getY(), mouseX, mouseY);
        }
    }

    public void renderCommandSuggestor(DrawContext context, int x, int y, int mouseX, int mouseY) {
        var window = ((ChatInputSuggestorAccessor) commandSuggestor).getWindow();
        if (window != null) {
            renderSuggestorWindow(window, context, y + this.valueEntry.getHeight(), mouseX, mouseY);
        } else {
            renderSuggestorMessages(context, x, y - 12);
        }
    }



    public void renderSuggestorWindow(ChatInputSuggestor.SuggestionWindow window, DrawContext context, int y, int mouseX, int mouseY) {
        var accessor = ((ChatInputSuggestorAccessor.SuggestionWindowAccessor) window);
        int i = Math.min(accessor.getSuggestions().size(), ((ChatInputSuggestorAccessor) commandSuggestor).getMaxSuggestionSize());
        boolean bl = accessor.getInWindowIndex() > 0;
        boolean bl2 = accessor.getSuggestions().size() > accessor.getInWindowIndex() + i;
        boolean bl3 = bl || bl2;
        boolean bl4 = accessor.getMouse().x != (float)mouseX || accessor.getMouse().y != (float)mouseY;
        if (bl4) {
            accessor.setMouse(new Vec2f((float)mouseX, (float)mouseY));
        }
        var area = accessor.getArea();
        var x = area.getX() + 3;
        y = y - 18 - i * 12;

        if (bl3) {
            context.fill(x, y - 1, x + area.getWidth(), y, ((ChatInputSuggestorAccessor) commandSuggestor).getColor());
            context.fill(x, y + area.getHeight(), x + area.getWidth(), y + area.getHeight() + 1, ((ChatInputSuggestorAccessor) commandSuggestor).getColor());
            int k;
            if (bl) {
                for(k = 0; k < area.getWidth(); ++k) {
                    if (k % 2 == 0) {
                        context.fill(x + k, y - 1, x + k + 1, y, -1);
                    }
                }
            }

            if (bl2) {
                for(k = 0; k < area.getWidth(); ++k) {
                    if (k % 2 == 0) {
                        context.fill(x + k, y + area.getHeight(), x + k + 1, y + area.getHeight() + 1, -1);
                    }
                }
            }
        }

        boolean bl5 = false;

        for(int l = 0; l < i; ++l) {
            Suggestion suggestion = accessor.getSuggestions().get(l + accessor.getInWindowIndex());
            context.fill(x, y + 12 * l, x + area.getWidth(), y + 12 * l + 12, ((ChatInputSuggestorAccessor) commandSuggestor).getColor());
            if (mouseX > x && mouseX < x + area.getWidth() && mouseY > y + 12 * l && mouseY < y + 12 * l + 12) {
                if (bl4) {
                    window.select(l + accessor.getInWindowIndex());
                }

                bl5 = true;
            }

            context.drawTextWithShadow(((ChatInputSuggestorAccessor) commandSuggestor).getTextRenderer(), suggestion.getText(), x + 1, y + 2 + 12 * l, l + accessor.getInWindowIndex() == accessor.getSelection() ? -256 : -5592406);
        }

        if (bl5) {
            Message message = accessor.getSuggestions().get(accessor.getSelection()).getTooltip();
            if (message != null) {
                context.drawTooltip(((ChatInputSuggestorAccessor) commandSuggestor).getTextRenderer(), Texts.toText(message), mouseX, mouseY);
            }
        }

    }

    public void renderSuggestorMessages(DrawContext context, int x, int y) {
        int i = 0;

        var accessor = (ChatInputSuggestorAccessor) commandSuggestor;

        for (Iterator<OrderedText> var3 = accessor.getMessages().iterator(); var3.hasNext(); ++i) {
            OrderedText orderedText = var3.next();
            var width = accessor.getTextRenderer().getWidth(orderedText);
            if (width > 200) {
                x = 2;
            }
            int j = y - 12 * i;
            context.fill(x - 1, j, x + accessor.getWidth() + 1, j + 12, accessor.getColor());
            context.drawTextWithShadow(accessor.getTextRenderer(), orderedText, x, j + 2, -1);
        }
    }
}
