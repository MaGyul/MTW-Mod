package dev.magyul.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import dev.magyul.api.TickTime;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class TPSCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var command = CommandManager.literal("tps");
        command.executes(context ->
                fabricTPS(context.getSource(), false)
        );
        command.then(CommandManager.argument("mspt", BoolArgumentType.bool())
                .executes(context ->
                        fabricTPS(context.getSource(), BoolArgumentType.getBool(context, "mspt"))
                )
        );
        command.then(CommandManager.argument("world", DimensionArgumentType.dimension())
                .executes(context ->
                        fabricTPS(context.getSource(), DimensionArgumentType.getDimensionArgument(context, "world"), false)
                )
                .then(CommandManager.argument("mspt", BoolArgumentType.bool())
                        .executes(context ->
                                fabricTPS(context.getSource(), DimensionArgumentType.getDimensionArgument(context, "world"), BoolArgumentType.getBool(context, "mspt"))
                        )
                )
        );
        dispatcher.register(command);
        dispatcher.register(CommandManager.literal("fabric").then(command));
    }

    private static int fabricTPS(ServerCommandSource source, boolean returnMspt) {
        var server = source.getServer();
        var feedback = new StringBuilder();
        for (var world : server.getWorlds()) {
            var key = world.getRegistryKey().getValue().toString();
            var mspt = ((TickTime) world).mtw$tickTime();
            var tps = 1000F / mspt;
            if (tps > 20F) tps = 20F;
            feedback.append("Dim (").append(key).append("): Mean tick time: ")
                    .append(String.format("%.3f", mspt)).append(" ms. Mean TPS: ")
                    .append(String.format("%.3f", tps)).append("\n");
        }
        var mspt = server.getAverageTickTime();
        var tps = 1000F / mspt;
        if (tps > 20F) tps = 20F;
        feedback.append("Overall: Mean tick time: ").append(String.format("%.3f", mspt))
                .append(" ms. Mean TPS: ").append(String.format("%.3f", tps));
        source.sendFeedback(() -> Text.literal(feedback.toString()), false);
        return (int) (returnMspt ? mspt : tps);
    }

    private static int fabricTPS(ServerCommandSource source, ServerWorld world, boolean returnMspt) {
        var feedback = new StringBuilder();
        var key = world.getRegistryKey().getValue().toString();
        var mspt = ((TickTime) world).mtw$tickTime();
        var tps = 1000F / mspt;
        if (tps > 20F) tps = 20F;
        feedback.append("Dim (").append(key).append("): Mean tick time: ")
                .append(String.format("%.3f", mspt)).append(" ms. Mean TPS: ")
                .append(String.format("%.3f", tps));
        source.sendFeedback(() -> Text.literal(feedback.toString()), false);
        return (int) (returnMspt ? mspt : tps);
    }
}
