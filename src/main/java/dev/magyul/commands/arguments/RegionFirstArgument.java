package dev.magyul.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.magyul.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RegionFirstArgument implements ArgumentType<String> {
    public static final String DELETE = "제거";
    private static final List<String> list = List.of("함경도", "평안도", "황해도", "강원도", "전라도", "경상도", "충청도", "경기도", "경상도", "바다", DELETE);

    private RegionFirstArgument() {
    }

    public static RegionFirstArgument first() {
        return new RegionFirstArgument();
    }

    public static String getFirst(final CommandContext<?> context, final String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public String parse(StringReader reader) {
        return StringUtil.readString(reader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        List<String> result = new ArrayList<>();
        StringUtil.copyPartialMatches(builder.getRemainingLowerCase(), list, result);
        for (var suggest : result) {
            builder.suggest(suggest);
        }
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return list;
    }
}
