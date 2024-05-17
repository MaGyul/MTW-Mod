package dev.magyul.mixin.client.accessor;

import com.mojang.brigadier.suggestion.Suggestion;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.text.OrderedText;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ChatInputSuggestor.class)
public interface ChatInputSuggestorAccessor {
    @Accessor
    List<OrderedText> getMessages();

    @Accessor
    int getWidth();

    @Accessor
    int getColor();

    @Accessor
    int getMaxSuggestionSize();

    @Accessor
    TextRenderer getTextRenderer();

    @Accessor
    ChatInputSuggestor.SuggestionWindow getWindow();

    @Mixin(ChatInputSuggestor.SuggestionWindow.class)
    interface SuggestionWindowAccessor {
        @Accessor
        Rect2i getArea();

        @Accessor
        List<Suggestion> getSuggestions();

        @Accessor
        int getInWindowIndex();

        @Accessor
        int getSelection();

        @Accessor
        Vec2f getMouse();

        @Accessor
        void setMouse(Vec2f val);

    }
}
