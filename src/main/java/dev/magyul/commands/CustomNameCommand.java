package dev.magyul.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CustomNameCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var command = CommandManager.literal("customname");
        command.requires(source -> source.hasPermissionLevel(2));
        command.executes(context -> execute(context.getSource(), null, null));
        command.then(CommandManager.argument("name", TextArgumentType.text())
                .executes(context -> execute(context.getSource(), null, TextArgumentType.getTextArgument(context, "name"))));
        command.then(CommandManager.literal("remove")
                .executes(context -> execute(context.getSource(), null)));
        command.then(CommandManager.argument("target", EntityArgumentType.entity())
                .executes(context -> execute(context.getSource(), EntityArgumentType.getEntity(context, "target"), null))
                .then(CommandManager.argument("name", TextArgumentType.text())
                        .executes(context -> execute(context.getSource(), EntityArgumentType.getEntity(context, "target"), TextArgumentType.getTextArgument(context, "name"))))
                .then(CommandManager.literal("remove")
                        .executes(context -> execute(context.getSource(), EntityArgumentType.getEntity(context, "target")))));
        dispatcher.register(command);
    }

    private static int execute(ServerCommandSource source, @Nullable Entity target, @Nullable Text changeName) throws CommandSyntaxException {
        var self = target == null;
        if (self) {
            target = source.getPlayerOrThrow();
        }
        var displayName = target.getDisplayName();
        if (changeName != null) {
            target.setCustomName(changeName);
            if (target instanceof ServerPlayerEntity player) {
                source.getServer().getPlayerManager().sendToAll(PlayerListS2CPacket.entryFromPlayer(List.of(player)));
            } else {
                target.setCustomNameVisible(true);
            }
        }
        var changedName = target.getDisplayName();
        source.sendFeedback(() -> {
            if (changeName == null) {
                if (self) {
                    return Text.translatable("mtwmod.command.customname.current.single", displayName);
                } else {
                    return Text.translatable("mtwmod.command.customname.current", displayName, displayName);
                }
            } else {
                if (self) {
                    return Text.translatable("mtwmod.command.customname.changed.single", changedName);
                } else {
                    return Text.translatable("mtwmod.command.customname.changed", displayName, changedName);
                }
            }
        }, true);
        return 0;
    }

    private static int execute(ServerCommandSource source, @Nullable Entity target) throws CommandSyntaxException {
        var self = target == null;
        if (self) {
            target = source.getPlayerOrThrow();
        }
        var displayName = target.getDisplayName();
        target.setCustomName(null);
        if (target instanceof ServerPlayerEntity player) {
            source.getServer().getPlayerManager().sendToAll(PlayerListS2CPacket.entryFromPlayer(List.of(player)));
        }
        target.setCustomNameVisible(false);
        source.sendFeedback(() -> {
            if (self) {
                return Text.translatable("mtwmod.command.customname.removed.single");
            } else {
                return Text.translatable("mtwmod.command.customname.removed", displayName);
            }
        }, true);
        return 0;
    }
}
