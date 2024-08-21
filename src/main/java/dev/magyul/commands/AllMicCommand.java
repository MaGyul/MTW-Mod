package dev.magyul.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.magyul.data.PlayerData;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class AllMicCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var command = CommandManager.literal("allmic");
        command.requires(source -> source.hasPermissionLevel(2));
        command.executes(context -> execute(context.getSource(), null));
        command.then(CommandManager.argument("target", EntityArgumentType.player())
                .executes(context -> execute(context.getSource(), EntityArgumentType.getPlayer(context, "target"))));
        dispatcher.register(command);
    }

    private static int execute(ServerCommandSource source, @Nullable ServerPlayerEntity target) throws CommandSyntaxException {
        var self = target == null;
        if (self) {
            target = source.getPlayerOrThrow();
        }
        var toggle = !PlayerData.isAllMic(target);
        PlayerData.setAllMic(target, toggle);
        var displayName = target.getDisplayName();
        source.sendFeedback(() -> {
            if (self) {
                return Text.translatable("mtwmod.command.allmic", Boolean.toString(toggle));
            } else {
                return Text.translatable("mtwmod.command.allmic.target", displayName, Boolean.toString(toggle));
            }
        }, true);
        return 0;
    }
}
