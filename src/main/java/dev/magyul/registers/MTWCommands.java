package dev.magyul.registers;

import com.mojang.brigadier.CommandDispatcher;
import dev.magyul.commands.*;
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
        CustomNameCommand.register(dispatcher, registryAccess);
    }
}
