package dev.magyul.mixin.client.cocoainput;

import com.llamalad7.mixinextras.sugar.Local;
import dev.magyul.cocoainput.util.Util;
import dev.magyul.cocoainput.wrapper.SignEditScreenWrapper;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSignEditScreen.class)
public abstract class SignEditScreenMixin {
    @Shadow
    public int currentRow;
    @Shadow public int ticksSinceOpened;
    @Unique
    private SignEditScreenWrapper wrapper;

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        wrapper = new SignEditScreenWrapper(This());
    }

    @Inject(method = "renderSignText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I", ordinal = 0))
    private void render(DrawContext context, CallbackInfo ci, @Local(ordinal = 0) int i, @Local(ordinal = 1) int j, @Local(ordinal = 4) int m, @Local(ordinal = 5) int n, @Local String string) {
        if (wrapper == null) return;
        if (wrapper.preeditBegin && n == currentRow && j >= 0) {
            var p = textRenderer().getWidth(string.substring(0, Math.min(j, string.length())));
            var x = (p - textRenderer().getWidth(string) / 2);
            if (this.ticksSinceOpened / 6 % 2 == 0) {
                Util.renderCursor(context, x + (Util.getUnderLineWidth(textRenderer()) * wrapper.markedPos), m, -16777216 | i);
            }
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
