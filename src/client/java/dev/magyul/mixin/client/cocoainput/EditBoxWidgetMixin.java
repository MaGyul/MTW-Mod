package dev.magyul.mixin.client.cocoainput;

import dev.magyul.cocoainput.util.Util;
import dev.magyul.cocoainput.wrapper.EditBoxWidgetWrapper;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.EditBoxWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EditBoxWidget.class)
public class EditBoxWidgetMixin {

    @Shadow @Final private TextRenderer textRenderer;
    @Unique
    protected EditBoxWidgetWrapper wrapper;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo cb) {
        wrapper = new EditBoxWidgetWrapper(This());
    }

    @Inject(method = "setFocused", at = @At("HEAD"))
    private void  setFocused(boolean focused, CallbackInfo ci) {
        wrapper.setFocused(focused);
    }

    @Inject(method = "renderContents", at = @At(value = "INVOKE", target = "Ljava/util/Objects;requireNonNull(Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 2, shift = At.Shift.BY, by = 2), locals = LocalCapture.CAPTURE_FAILHARD)
    private void renderContents(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci, String string, int i, boolean bl, boolean bl2, int j, int k) {
//        if (wrapper.preeditBegin) {
//            var width = Util.getUnderLineWidth(textRenderer);
//            Util.renderCursor(context, (j - width) + (width * wrapper.markedPos), k, -3092272);
//            Util.renderUnderLine(context, textRenderer, wrapper.length, j - width, k, -3092272);
//        }
    }

    @Redirect(method = "renderContents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/EditBoxWidget;isFocused()Z", ordinal = 1))
    private boolean renderContents(EditBoxWidget instance) {
        if (wrapper.cursorVisible) {
            return instance.isFocused();
        } else {
            return false;
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"))
    private void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if ((This().isNarratable() && This().isFocused()) && wrapper.preeditBegin) {
            if (Screen.isSelectAll(keyCode) ||
                    Screen.isCopy(keyCode) ||
                    Screen.isPaste(keyCode) ||
                    Screen.isCut(keyCode) ||
                    Util.isEnter(keyCode)) {
                wrapper.insertText("");
            }
        }
    }

    @Unique
    private EditBoxWidget This() {
        return (EditBoxWidget) (Object) this;
    }
}
