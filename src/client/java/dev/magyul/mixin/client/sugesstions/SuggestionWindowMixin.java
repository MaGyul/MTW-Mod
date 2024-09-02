package dev.magyul.mixin.client.sugesstions;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.brigadier.suggestion.Suggestion;
import dev.magyul.api.CustomSuggestionAccessor;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ChatInputSuggestor.SuggestionWindow.class, priority = 1001)
public class SuggestionWindowMixin {

    @ModifyVariable(
            at = @At("STORE"),
            method = {"render"},
            ordinal = 0,
            order = 1001
    )
    private Suggestion render$captureSuggestion(Suggestion suggestion, @Share("customSuggestions") LocalRef<CustomSuggestionAccessor> customSuggestions) {
        customSuggestions.set((CustomSuggestionAccessor) suggestion);
        return suggestion;
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)I", ordinal = 0))
    private int render$drawTextWithShadow(DrawContext context, TextRenderer textRenderer, String __, int x, int y, int color, @Share("customSuggestions") LocalRef<CustomSuggestionAccessor> customSuggestions) {
        return context.drawTextWithShadow(textRenderer, customSuggestions.get().mtwmod$getFormattedText(), x, y, color);
    }
}
