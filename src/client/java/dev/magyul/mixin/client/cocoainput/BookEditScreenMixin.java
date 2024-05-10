package dev.magyul.mixin.client.cocoainput;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.magyul.cocoainput.util.Util;
import dev.magyul.cocoainput.wrapper.BookEditScreenWrapper;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BookEditScreen.class)
public abstract class BookEditScreenMixin {
    @Shadow
    protected abstract BookEditScreen.Position absolutePositionToScreenPosition(BookEditScreen.Position position);
    @Shadow
    public String title;
    @Unique
    private BookEditScreenWrapper wrapper;

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo cb) {
        wrapper = new BookEditScreenWrapper(This());
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Lnet/minecraft/text/OrderedText;)I", shift = At.Shift.BEFORE))
    private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci, @Local LocalRef<OrderedText> localRef) {
        if (wrapper == null) return;
        if (!wrapper.cursorVisible) {
            var orderedText = OrderedText.concat(OrderedText.styledForwardsVisitedString(this.title, Style.EMPTY));
            localRef.set(orderedText);
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/OrderedText;IIIZ)I"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci, int i, int j, boolean bl, OrderedText orderedText, int k, int l) {
        if (wrapper == null) return;
        if (wrapper.preeditBegin) {
            var x = i + 36 + (114 - l) / 2;
            var y = 50;
            var width = Util.getUnderLineWidth(textRenderer());
            Util.renderCursor(context, x + (width * wrapper.markedPos), y, -16777216);
            Util.renderUnderLine(context, textRenderer(), wrapper.length, x, y, 0, false);
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/BookEditScreen;drawCursor(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/gui/screen/ingame/BookEditScreen$Position;Z)V", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void drawCursor(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci, int i, int j, int n, BookEditScreen.PageContent pageContent) {
        if (wrapper == null) return;
        if (!wrapper.cursorVisible) {
            ci.cancel();
        }
        if (wrapper.preeditBegin) {
            var position = absolutePositionToScreenPosition(pageContent.position);
            var x = position.x;
            Util.renderCursor(context, x + (Util.getUnderLineWidth(textRenderer()) * wrapper.markedPos), position.y, -16777216);
            Util.renderUnderLine(context, textRenderer(), wrapper.length, x, position.y, 0, false);
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
    private BookEditScreen This() {
        return (BookEditScreen) (Object) this;
    }
}