package dev.magyul.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.magyul.data.PlayerData;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class MoveCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var command = CommandManager.literal("move");
        command.requires(source -> source.hasPermissionLevel(2));
        command.then(CommandManager.literal("here")
                .executes(context -> here(context.getSource(), false))
                .then(CommandManager.literal("remove")
                        .executes(context -> here(context.getSource(), true))));
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

    private static int here(ServerCommandSource source, boolean remove) throws CommandSyntaxException {
        var player = source.getPlayerOrThrow();
        var world = player.getServerWorld();
        Text text;
        if (remove) {
            PlayerData.setMoveHere(player, world, null);
            text = Text.translatable("mtwmod.command.move.here.remove", world.getRegistryKey().getValue().toString());
        } else {
            var pos = player.getPos();
            PlayerData.setMoveHere(player, world, pos);
            text = Text.translatable("mtwmod.command.move.here", world.getRegistryKey().getValue().toString(), formatFloat(pos.x), formatFloat(pos.y), formatFloat(pos.z));
        }
        source.sendFeedback(() -> text, true);
        return 0;
    }

    private static int execute(ServerCommandSource source, ServerWorld world, Collection<ServerPlayerEntity> players, boolean here) throws CommandSyntaxException {
        if (players.isEmpty()) {
            var player = source.getPlayerOrThrow();
            teleport(player, world, PlayerData.getMoveHere(player, world), player.getYaw(), player.getPitch());
            source.sendFeedback(() ->
                    Text.translatable("mtwmod.command.move", world.getRegistryKey().getValue().toString()), true);
        } else {
            if (here) {
                var player = source.getPlayerOrThrow();
                for (var target : players) {
                    teleport(target, world, player.getPos(), target.getYaw(), target.getPitch());
                }
            } else {
                for (var player : players) {
                    teleport(player, world, PlayerData.getMoveHere(player, world), player.getYaw(), player.getPitch());
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

    private static void teleport(ServerPlayerEntity player, ServerWorld world, Vec3d vec3d, float yaw, float pitch) {
        player.teleport(world, vec3d.x, vec3d.y, vec3d.z, yaw, pitch);
    }

    private static String formatFloat(double d) {
        return String.format(Locale.ROOT, "%f", d);
    }
}
