package dev.magyul.mixin.client.cocoainput;

import dev.magyul.cocoainput.util.Util;
import dev.magyul.cocoainput.wrapper.EditBoxWidgetWrapper;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EditBoxWidget.class)
public abstract class EditBoxWidgetMixin extends ScrollableWidget {
    @Unique
    protected EditBoxWidgetWrapper wrapper;

    public EditBoxWidgetMixin(int i, int j, int k, int l, Text text) {
        super(i, j, k, l, text);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo cb) {
        wrapper = new EditBoxWidgetWrapper(This());
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        wrapper.setFocused(focused);
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
