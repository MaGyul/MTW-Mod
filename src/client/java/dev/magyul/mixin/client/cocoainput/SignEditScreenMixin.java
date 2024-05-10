package dev.magyul.mixin.client.cocoainput;

import dev.magyul.cocoainput.util.Util;
import dev.magyul.cocoainput.wrapper.SignEditScreenWrapper;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AbstractSignEditScreen.class)
public abstract class SignEditScreenMixin {
    @Shadow
    public int currentRow;
    @Unique
    private SignEditScreenWrapper wrapper;

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        wrapper = new SignEditScreenWrapper(This());
    }

    @Inject(method = "renderSignText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    private void render(DrawContext context, CallbackInfo ci, Vector3f vector3f, int i, boolean bl, int j, int k, int l, int m, int n, String string, int o) {
        if (wrapper == null) return;
        if (wrapper.preeditBegin && n == currentRow && j >= 0) {
            var p = textRenderer().getWidth(string.substring(0, Math.min(j, string.length())));
            var x = (p - textRenderer().getWidth(string) / 2);
            Util.renderCursor(context, x + (Util.getUnderLineWidth(textRenderer()) * wrapper.markedPos), m, -16777216 | i);
            Util.renderUnderLine(context, textRenderer(), wrapper.length, x, m, i, false);
        }
    }

    @Redirect(method = "renderSignText", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/ingame/AbstractSignEditScreen;ticksSinceOpened:I"))
    private int render(AbstractSignEditScreen instance) {
        if (wrapper == null) return instance.ticksSinceOpened;
        if (wrapper.cursorVisible) {
            return instance.ticksSinceOpened;
        } else {
            return 6;
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"))
    private void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (wrapper == null) return;
        if (wrapper.preeditBegin) {
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
    private TextRenderer textRenderer() {
        return This().textRenderer;
    }

    @Unique
    private AbstractSignEditScreen This() {
        return (AbstractSignEditScreen) (Object) this;
    }
}
