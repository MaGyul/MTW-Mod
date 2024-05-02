package dev.magyul.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.List;

public class MoveCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var command = CommandManager.literal("move");
        command.requires(source -> source.hasPermissionLevel(2));
        command.then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
                .executes(context -> execute(context.getSource(), DimensionArgumentType.getDimensionArgument(context, "dimension"), List.of(), false))
                .then(CommandManager.argument("targets", EntityArgumentType.players())
                        .executes(context -> execute(context.getSource(), DimensionArgumentType.getDimensionArgument(context, "dimension"), EntityArgumentType.getPlayers(context, "targets"), false))
                        .then(CommandManager.argument("move here", BoolArgumentType.bool())
                                .executes(context -> execute(context.getSource(), DimensionArgumentType.getDimensionArgument(context, "dimension"), EntityArgumentType.getPlayers(context, "targets"), BoolArgumentType.getBool(context, "move here")))
                        )
                )
        );
        dispatcher.register(command);
    }

    private static int execute(ServerCommandSource source, ServerWorld world, Collection<ServerPlayerEntity> players, boolean here) throws CommandSyntaxException {
        if (players.isEmpty()) {
            var player = source.getPlayerOrThrow();
            player.teleport(world, player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
            source.sendFeedback(() ->
                    Text.translatable("mtwmod.command.move", world.getRegistryKey().getValue().toString()), true);
        } else {
            if (here) {
                var player = source.getPlayerOrThrow();
                for (var target : players) {
                    target.teleport(world, player.getX(), player.getY(), player.getZ(), target.getYaw(), target.getPitch());
                }
            } else {
                for (var player : players) {
                    player.teleport(world, player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
                }
            }
            if (players.size() == 1) {
                source.sendFeedback(() ->
                        Text.translatable("mtwmod.command.move.single", players.iterator().next().getDisplayName(), world.getRegistryKey().getValue().toString()), true);
            } else {
                source.sendFeedback(() ->
                        Text.translatable("mtwmod.command.move.multiple", players.size(), world.getRegistryKey().getValue().toString()), true);
            }
        }
        return 0;
    }
}
