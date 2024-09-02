package dev.magyul.mixin.client.sugesstions;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.brigadier.suggestion.Suggestion;
import dev.magyul.util.SuggestionUtil;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Suggestion.class, remap = false)
public class ButterSuggestionMixin {

    @Shadow @Final private String text;

    @ModifyReturnValue(method = {
            "getFormattedText()Lnet/minecraft/text/Text;",
            "getFormattedText()Lnet/minecraft/class_2561;"
    }, at = @At("RETURN"), remap = false)
    private Text getFormattedText(Text original) {
        var entry = SuggestionUtil.getPlayerListEntry(text);
        if (entry != null) {
            return Text.translatable("%s (%s)", Text.literal(this.text), entry.getDisplayName());
        }

        return original;
    }
}
