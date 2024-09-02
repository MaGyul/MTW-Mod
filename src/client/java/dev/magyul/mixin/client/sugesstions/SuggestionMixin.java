package dev.magyul.mixin.client.sugesstions;

import com.mojang.brigadier.suggestion.Suggestion;
import dev.magyul.api.CustomSuggestionAccessor;
import dev.magyul.util.SuggestionUtil;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Suggestion.class, remap = false)
public class SuggestionMixin implements CustomSuggestionAccessor {

    @Shadow @Final private String text;

    @Override
    public Text mtwmod$getFormattedText() {
        var entry = SuggestionUtil.getPlayerListEntry(text);
        if (entry != null) {
            return Text.translatable("%s (%s)", Text.literal(this.text), entry.getDisplayName());
        }

        return Text.of(text);
    }
}
