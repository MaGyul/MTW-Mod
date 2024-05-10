package dev.magyul.mixin.client.cocoainput;

import dev.magyul.cocoainput.util.Util;
import dev.magyul.cocoainput.wrapper.TextFieldWidgetWrapper;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
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

@Mixin(TextFieldWidget.class)
public abstract class TextFieldWidgetMixin {
    @Shadow @Final public TextRenderer textRenderer;
    @Unique
    protected TextFieldWidgetWrapper wrapper;

    @Inject(method = "<init>(Lnet/minecraft/client/font/TextRenderer;IIIILnet/minecraft/client/gui/widget/TextFieldWidget;Lnet/minecraft/text/Text;)V", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        if (wrapper == null) wrapper = new TextFieldWidgetWrapper(This());
    }

    @Inject(method = "setFocusUnlocked", at = @At("HEAD"))
    private void setFocusUnlocked(boolean focusUnlocked, CallbackInfo ci) {
        wrapper.setFocusUnlocked(focusUnlocked);
    }

    @Inject(method = "renderWidget", at = @At(value = "INVOKE", target = "Ljava/lang/String;isEmpty()Z", ordinal = 1, shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    private void renderWidget(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci, int i, int j, String string, boolean bl, boolean bl2, int k, int l, int m, int n, boolean bl3, int o) {
        wrapper.setFocused(This().isFocused());
        if (wrapper.preeditBegin) {
            Util.renderCursor(context, o + (Util.getUnderLineWidth(textRenderer) * wrapper.markedPos), l, -3092272);
            Util.renderUnderLine(context, textRenderer, wrapper.length, o, l, i);
        }
    }

    @Redirect(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;isFocused()Z", ordinal = 1))
    private boolean renderWidget(TextFieldWidget instance) {
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
    private TextFieldWidget This() {
        return (TextFieldWidget) (Object) this;
    }
}
