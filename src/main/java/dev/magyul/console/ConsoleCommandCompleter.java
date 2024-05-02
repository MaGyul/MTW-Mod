package dev.magyul.console;

import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.List;

public class ConsoleCommandCompleter implements Completer {
    private final BrigadierCommandCompleter brigadierCompleter;

    public ConsoleCommandCompleter(MinecraftDedicatedServer server) {
        this.brigadierCompleter = new BrigadierCommandCompleter(server);
    }

    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        addCompletions(line, candidates);
    }

    private void addCompletions(final ParsedLine line, final List<Candidate> candidates) {
        this.brigadierCompleter.complete(line, candidates);
    }
}
