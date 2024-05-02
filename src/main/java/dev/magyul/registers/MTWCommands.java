package dev.magyul.registers;

import com.mojang.brigadier.CommandDispatcher;
import dev.magyul.commands.AllMicCommand;
import dev.magyul.commands.MoveCommand;
import dev.magyul.commands.RegionCommand;
import dev.magyul.commands.TPSCommand;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class MTWCommands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        AllMicCommand.register(dispatcher);
        MoveCommand.register(dispatcher);
        RegionCommand.register(dispatcher);
        TPSCommand.register(dispatcher);
    }
}
