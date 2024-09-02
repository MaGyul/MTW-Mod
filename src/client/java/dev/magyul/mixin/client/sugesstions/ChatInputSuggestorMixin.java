package dev.magyul.mixin.client.sugesstions;

import com.mojang.brigadier.suggestion.Suggestion;
import dev.magyul.api.CustomSuggestionAccessor;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ChatInputSuggestor.class, priority = 1001)
public class ChatInputSuggestorMixin {

    @Redirect(method = "show", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/suggestion/Suggestion;getText()Ljava/lang/String;", remap = false))
    private String show$getText(Suggestion suggestion) {
        return ((CustomSuggestionAccessor) suggestion).mtwmod$getFormattedText().getString();
    }
}
