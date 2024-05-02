package dev.magyul.console;

import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecrell.terminalconsole.SimpleTerminalConsole;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

import java.nio.file.Paths;

public class MTWConsole extends SimpleTerminalConsole {

    private final MinecraftDedicatedServer server;

    public MTWConsole(MinecraftDedicatedServer server) {
        this.server = server;
    }

    @Override
    protected LineReader buildReader(LineReaderBuilder builder) {
        return super.buildReader(builder
                .appName("fabric")
                .variable(LineReader.HISTORY_FILE, Paths.get(".console_history"))
                .completer(new ConsoleCommandCompleter(this.server))
                .highlighter(new BrigadierCommandHighlighter(this.server)));
    }

    @Override
    protected boolean isRunning() {
        return !this.server.isStopped() && this.server.isRunning();
    }

    @Override
    protected void runCommand(String command) {
        this.server.enqueueCommand(command, this.server.getCommandSource());
    }

    @Override
    protected void shutdown() {
        this.server.stop(false);
    }
}
