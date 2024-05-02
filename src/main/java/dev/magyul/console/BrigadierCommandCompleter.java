package dev.magyul.console;

import com.google.common.base.Suppliers;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestion;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import org.jetbrains.annotations.NotNull;
import org.jline.reader.Candidate;
import org.jline.reader.ParsedLine;

import java.util.List;
import java.util.function.Supplier;

public class BrigadierCommandCompleter {
    private final Supplier<ServerCommandSource> serverCommandSource;
    private final MinecraftDedicatedServer server;

    public BrigadierCommandCompleter(MinecraftDedicatedServer server) {
        this.server = server;
        this.serverCommandSource = Suppliers.memoize(this.server::getCommandSource);
    }

    public void complete(final @NotNull ParsedLine line, final @NotNull List<Candidate> candidates) {
        if (this.server.getOverworld() == null) {
            return;
        }
        final var dispatcher = this.server.getCommandManager().getDispatcher();
        final var results = dispatcher.parse(prepareStringReader(line.line()), this.serverCommandSource.get());
        this.addCandidates(
                candidates,
                dispatcher.getCompletionSuggestions(results, line.cursor()).join().getList()
        );
    }

    private void addCandidates(
            final @NotNull List<Candidate> candidates,
            final @NotNull List<Suggestion> brigSuggestions
    ) {
        for (var suggestion : brigSuggestions) {
            if (suggestion.getText().isEmpty()) continue;
            candidates.add(toCandidate(suggestion));
        }
    }

    private static @NotNull Candidate toCandidate(final @NotNull Suggestion completion) {
        final var suggestionText = completion.getText();
        final var tooltip = completion.getTooltip();
        final var suggestionTooltip = tooltip != null ? tooltip.getString() : null;
        return new Candidate(
                suggestionText,
                suggestionText,
                null,
                suggestionTooltip,
                null,
                null,
                false
        );
    }

    static @NotNull StringReader prepareStringReader(final @NotNull String line) {
        final var stringReader = new StringReader(line);
        if (stringReader.canRead() && stringReader.peek() == '/') {
            stringReader.skip();
        }
        return stringReader;
    }
}
