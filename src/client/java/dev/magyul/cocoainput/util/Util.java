package dev.magyul.cocoainput.util;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Marker;

import java.util.function.UnaryOperator;

public class Util {
    private static final UnaryOperator<Style> underLineStyle = style -> style.withUnderline(true);
    public static MutableText Underline = Text.literal("　").styled(underLineStyle);
    public static String getMarkerHeader(Marker marker) {
        return "[" + marker.getName() + "] ";
    }
    public static void renderCursor(DrawContext context, int x, int y, int color) {
        context.fill(RenderLayer.getGuiOverlay(), x, y - 1, x + 1, y + 1 + 9, color);
    }
    public static void renderUnderLine(DrawContext context, TextRenderer renderer, int length, int x, int y, int color) {
        renderUnderLine(context, renderer, length, x, y, color, true);
    }
    public static void renderUnderLine(DrawContext context, TextRenderer renderer, int length, int x, int y, int color, boolean shadow) {
        context.drawText(renderer, underLineRepeat(length), x, y, color, shadow);
    }
    public static MutableText underLineRepeat(int count) {
        return Text.literal("　".repeat(count)).styled(underLineStyle);
    }
    public static int getUnderLineWidth(TextRenderer renderer) {
        return renderer.getWidth(Underline);
    }
    public static boolean isEnter(int keyCode) {
        return keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER;
    }
}
