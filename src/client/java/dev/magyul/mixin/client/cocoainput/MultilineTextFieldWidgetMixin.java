package dev.magyul.mixin.client.cocoainput;

import dev.magyul.others.cocoainput.util.Util;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(targets = "arm32x.minecraft.commandblockide.client.gui.MultilineTextFieldWidget")
public class MultilineTextFieldWidgetMixin extends TextFieldWidgetMixin {

    @Inject(method = "renderWidget", at = @At(value = "INVOKE", target = "Larm32x/minecraft/commandblockide/client/gui/MultilineTextFieldWidget;isFocused()Z", ordinal = 2, shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    private void renderWidget(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci, int textColor, int x, int y, long timeSinceLastSwitchFocusMs, boolean showCursor, boolean lineCursor, int cursorLine, int cursorX, int cursorY) {
        wrapper.setFocused(This().isFocused());
        if (wrapper.preeditBegin) {
            Util.renderCursor(context, cursorX + 1 + (Util.getUnderLineWidth(textRenderer) * wrapper.markedPos), cursorY, -3092272);
            Util.renderUnderLine(context, textRenderer, wrapper.length, cursorX + 1, cursorY, textColor);
        }
    }

    // TODO: 모드 업데이트 되면 활성화!
//    @Redirect(method = "renderWidget", at = @At(value = "INVOKE", target = "Larm32x/minecraft/commandblockide/client/gui/MultilineTextFieldWidget;isFocused()Z", ordinal = 1))
//    private boolean renderWidget(arm32x.minecraft.commandblockide.client.gui.MultilineTextFieldWidget instance) {
//        if (wrapper.cursorVisible) {
//            return instance.isFocused();
//        } else {
//            return false;
//        }
//    }

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
