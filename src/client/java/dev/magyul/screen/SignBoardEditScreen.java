package dev.magyul.screen;

import dev.magyul.blocks.SignBoard;
import dev.magyul.blocks.entities.SignBoardEntity;
import dev.magyul.blocks.entities.SignBoardText;
import net.minecraft.block.BlockState;
import net.minecraft.block.WoodType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

@SuppressWarnings("DataFlowIssue")
public class SignBoardEditScreen extends Screen {
    private final SignBoardEntity boardEntity;
    public SignBoardText text;
    private String message;
    protected final WoodType signType;
    public int ticksSinceOpened;
    @Nullable
    public SelectionManager selectionManager;
    private static final Vector3f TEXT_SCALE = new Vector3f(0.9765628F, 0.9765628F, 0.9765628F);

    public SignBoardEditScreen(SignBoardEntity boardEntity, boolean filtered) {
        this(boardEntity, filtered, Text.translatable("sign.edit"));
    }

    public SignBoardEditScreen(SignBoardEntity boardEntity, boolean filtered, Text title) {
        super(title);
        this.boardEntity = boardEntity;
        this.text = boardEntity.getText();
        this.signType = SignBoard.getWoodType(boardEntity.getCachedState().getBlock());
        this.message = text.getMessage(filtered).getString();
    }

    @Override
    protected void init() {
        addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, (button) ->
                finishEditing()).dimensions(width / 2 - 100, height / 4 + 144, 200, 20).build());
        selectionManager = new SelectionManager(
                () -> message,
                this::setMessage,
                SelectionManager.makeClipboardGetter(client),
                SelectionManager.makeClipboardSetter(client),
                (string) -> string.length() == 1);
    }

    @Override
    public void tick() {
        ++ticksSinceOpened;

    }

    private void translateForRender(DrawContext context, BlockState state) {

    }

    private void renderSign(DrawContext context) {
        var blockState = boardEntity.getCachedState();
    }

    private void renderSignText(DrawContext context) {
        context.getMatrices().translate(0.0, 0.0, 4.0);
        Vector3f vector3f = TEXT_SCALE;
        context.getMatrices().scale(vector3f.x(), vector3f.y(), vector3f.z());
        int color = text.isGlowing() ? text.getColor().getSignColor() : 0;//SignBoardEntityRenderer.getColor(text);
        boolean bl = ticksSinceOpened / 6 % 2 == 0;
        int start = selectionManager.getSelectionStart();
        int end = selectionManager.getSelectionEnd();
        int l = boardEntity.getTextLineHeight() / 2;
        int height = boardEntity.getTextLineHeight() - l;

        String string = message;
        if (string != null) {
            if (textRenderer.isRightToLeft()) {
                string = textRenderer.mirror(string);
            }

            int width = -textRenderer.getWidth(string) / 2;
            context.drawText(textRenderer, string, width, height, color, false);
            if (start >= 0 && bl) {
                int p = textRenderer.getWidth(string.substring(0, Math.max(Math.min(start, string.length()), 0)));
                width = p - textRenderer.getWidth(string) / 2;
                if (start >= string.length()) {
                    context.drawText(textRenderer, "_", width, height, color, false);
                }
            }
        }
        string = message;
        if (string != null && start >= 0) {
            int o = textRenderer.getWidth(string.substring(0, Math.max(Math.min(start, string.length()), 0)));
            int width = o - textRenderer.getWidth(string) / 2;
            if (bl && start < string.length()) {
                context.fill(width, height - 1, width + 1, height + boardEntity.getTextLineHeight(), -16777216 | color);
            }

            if (end != start) {
                int q = Math.min(start, end);
                int r = Math.max(start, end);
                int s = this.textRenderer.getWidth(string.substring(0, q)) - this.textRenderer.getWidth(string) / 2;
                int t = this.textRenderer.getWidth(string.substring(0, r)) - this.textRenderer.getWidth(string) / 2;
                int u = Math.min(s, t);
                int v = Math.max(s, t);
                context.fill(RenderLayer.getGuiTextHighlight(), u, height, v, height + boardEntity.getTextLineHeight(), -16776961);
            }
        }
    }

    public final void setMessage(String message) {
        this.message = message;
        this.text = this.text.withMessage(Text.literal(message));
        this.boardEntity.setText(this.text);
    }

    private void finishEditing() {
        client.setScreen(null);
    }
}
