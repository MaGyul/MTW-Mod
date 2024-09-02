package dev.magyul.mixin.client.cocoainput;

import arm32x.minecraft.commandblockide.client.gui.MultilineTextFieldWidget;
import com.llamalad7.mixinextras.sugar.Local;
import dev.magyul.cocoainput.util.Util;
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

@Mixin(MultilineTextFieldWidget.class)
public class MultilineTextFieldWidgetMixin extends TextFieldWidgetMixin {

    @Shadow(remap = false) @Final private static long CURSOR_BLINK_INTERVAL_MS;

    @Inject(method = "renderWidget", at = @At(value = "INVOKE", target = "Larm32x/minecraft/commandblockide/client/gui/MultilineTextFieldWidget;isFocused()Z", ordinal = 2))
    private void renderWidget(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci,
                              @Local(ordinal = 2) int textColor, @Local(ordinal = 6) int cursorX, @Local(ordinal = 7) int cursorY,
                              @Local long timeSinceLastSwitchFocusMs) {
        wrapper.setFocused(This().isFocused());
        if (wrapper.preeditBegin) {
            if (showCursor(timeSinceLastSwitchFocusMs)) {
                Util.renderCursor(context, cursorX + 1 + (Util.getUnderLineWidth(textRenderer) * wrapper.markedPos), cursorY, -3092272);
            }
            Util.renderUnderLine(context, textRenderer, wrapper.length, cursorX + 1, cursorY, textColor);
        }
    }

    @Redirect(method = "renderWidget", at = @At(value = "INVOKE", target = "Larm32x/minecraft/commandblockide/client/gui/MultilineTextFieldWidget;isFocused()Z", ordinal = 1))
    private boolean renderWidget(MultilineTextFieldWidget instance) {
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
    private boolean showCursor(long timeSinceLastSwitchFocusMs) {
        return This().isFocused() && timeSinceLastSwitchFocusMs / CURSOR_BLINK_INTERVAL_MS % 2 == 0;
    }

    @Unique
    private TextFieldWidget This() {
        return (TextFieldWidget) (Object) this;
    }
}
