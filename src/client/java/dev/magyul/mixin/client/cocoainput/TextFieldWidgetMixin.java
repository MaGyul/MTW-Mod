package dev.magyul.mixin.client.cocoainput;

import com.llamalad7.mixinextras.sugar.Local;
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

@Mixin(TextFieldWidget.class)
public abstract class TextFieldWidgetMixin {
    @Shadow @Final public TextRenderer textRenderer;
    @Shadow private long lastSwitchFocusTime;
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

    @Inject(method = "renderWidget", at = @At(value = "INVOKE", target = "Ljava/lang/String;isEmpty()Z", ordinal = 1))
    private void renderWidget(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci,
                              @Local(ordinal = 2) int i, @Local(ordinal = 5) int l, @Local(ordinal = 8) int o,
                              @Local(ordinal = 0) boolean bl) {
        wrapper.setFocused(This().isFocused());
        if (wrapper.preeditBegin) {
            if (showCursor(bl)) {
                Util.renderCursor(context, o + (Util.getUnderLineWidth(textRenderer) * wrapper.markedPos), l, -3092272);
            }
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
    private boolean showCursor(boolean bl) {
        return This().isFocused() && (net.minecraft.util.Util.getMeasuringTimeMs() - this.lastSwitchFocusTime) / 300L % 2L == 0L && bl;
    }

    @Unique
    private TextFieldWidget This() {
        return (TextFieldWidget) (Object) this;
    }
}
