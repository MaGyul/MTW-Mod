package dev.magyul.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.magyul.util.StringUtil;
import net.minecraft.command.CommandSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PlayerNameArgumentType implements ArgumentType<String> {

    private PlayerNameArgumentType() {
    }

    public static PlayerNameArgumentType pn() {
        return new PlayerNameArgumentType();
    }

    public static String getName(final CommandContext<?> context, final String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public String parse(final StringReader reader) {
        return reader.readUnquotedString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        List<String> result = new ArrayList<>();
        if (context.getSource() instanceof CommandSource source) {
            StringUtil.copyPartialMatches(builder.getRemainingLowerCase(), source.getPlayerNames(), result);
        }
        result.forEach(builder::suggest);
        return builder.buildFuture();
    }

    @Override
    public String toString() {
        return "string()";
    }

    @Override
    public Collection<String> getExamples() {
        return List.of("Mojang_");
    }
}

