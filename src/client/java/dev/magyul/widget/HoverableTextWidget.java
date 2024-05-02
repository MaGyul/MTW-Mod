package dev.magyul.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class HoverableTextWidget extends ButtonWidget {
    private final TextRenderer textRenderer;
    private final Text text;
    private final Text hoverText;
    private final Supplier<List<Text>> tooltip;

    public HoverableTextWidget(int x, int y, int width, int height, Text text, Supplier<List<Text>> tooltip, TextRenderer textRenderer) {
        super(x, y, width, height, text, b -> {}, DEFAULT_NARRATION_SUPPLIER);
        this.textRenderer = textRenderer;
        this.text = text;
        this.hoverText = Texts.setStyleIfAbsent(text.copy(), Style.EMPTY.withUnderline(true));
        this.tooltip = tooltip;
    }

    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        Text text = this.isSelected() ? this.hoverText : this.text;
        context.drawTextWithShadow(this.textRenderer, text, this.getX(), this.getY(), 16777215 | MathHelper.ceil(this.alpha * 255.0F) << 24);
        if (isSelected()) {
            context.drawTooltip(this.textRenderer, tooltip.get(), mouseX, mouseY);
        }
    }
}
