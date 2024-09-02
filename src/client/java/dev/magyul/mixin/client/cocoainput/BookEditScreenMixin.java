package dev.magyul.mixin.client.cocoainput;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
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

@Mixin(BookEditScreen.class)
public abstract class BookEditScreenMixin {
    @Shadow
    protected abstract BookEditScreen.Position absolutePositionToScreenPosition(BookEditScreen.Position position);
    @Shadow
    public String title;
    @Shadow private int tickCounter;
    @Unique
    private BookEditScreenWrapper wrapper;

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo cb) {
        wrapper = new BookEditScreenWrapper(This());
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/OrderedText;concat(Lnet/minecraft/text/OrderedText;Lnet/minecraft/text/OrderedText;)Lnet/minecraft/text/OrderedText;"))
    private OrderedText render$concat(OrderedText first, OrderedText second, Operation<OrderedText> original) {
        if (wrapper != null && !wrapper.cursorVisible) {
            return OrderedText.styledForwardsVisitedString(this.title, Style.EMPTY);
        }
        return original.call(first, second);
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/OrderedText;IIIZ)I"))
    private int render$drawText(DrawContext instance, TextRenderer textRenderer, OrderedText text, int x, int y, int color, boolean shadow, Operation<Integer> original) {
        if (wrapper != null && wrapper.preeditBegin) {
            var width = Util.getUnderLineWidth(textRenderer);
            if (this.tickCounter / 6 % 2 == 0) {
                Util.renderCursor(instance, x + (width * wrapper.markedPos), y, -16777216);
            }
            Util.renderUnderLine(instance, textRenderer, wrapper.length, x, y, 0, false);
        }
        return original.call(instance, textRenderer, text, x, y, color, shadow);
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/BookEditScreen;drawCursor(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/gui/screen/ingame/BookEditScreen$Position;Z)V"))
    private void render$drawCursor(BookEditScreen instance, DrawContext context, BookEditScreen.Position position, boolean atEnd, Operation<Void> original, @Local BookEditScreen.PageContent pageContent) {
        if (wrapper != null) {
            if (wrapper.preeditBegin) {
                position = absolutePositionToScreenPosition(position);
                if (this.tickCounter / 6 % 2 == 0) {
                    Util.renderCursor(context, position.x + (Util.getUnderLineWidth(textRenderer()) * wrapper.markedPos), position.y, -16777216);
                }
                Util.renderUnderLine(context, textRenderer(), wrapper.length, position.x, position.y, 0, false);
            }
            if (!wrapper.cursorVisible) {
                return;
            }
        }

        original.call(instance, context, position, atEnd);
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