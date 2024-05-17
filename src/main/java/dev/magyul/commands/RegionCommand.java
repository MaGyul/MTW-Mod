package dev.magyul.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.magyul.commands.arguments.RegionFirstArgument;
import dev.magyul.commands.arguments.RegionSecondArgument;
import dev.magyul.data.WorldData;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;

import static java.lang.String.format;
import static net.minecraft.text.Text.literal;

public class RegionCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var command = CommandManager.literal("region");
        command.requires(source -> source.hasPermissionLevel(2));
        command.executes(context -> execute(context.getSource(), null, null));
        command.then(CommandManager.argument("first", RegionFirstArgument.first())
                .executes(context -> execute(context.getSource(), RegionFirstArgument.getFirst(context, "first"), null))
                .then(CommandManager.argument("second", RegionSecondArgument.second())
                        .executes(context ->
                                execute(context.getSource(), RegionFirstArgument.getFirst(context, "first"), RegionSecondArgument.getSecond(context, "second"))
                        )
                )
        );
        var tp = CommandManager.literal("tp");
        tp.then(CommandManager.argument("target", EntityArgumentType.players())
                .then(CommandManager.argument("first", RegionFirstArgument.first())
                        .then(CommandManager.argument("second", RegionSecondArgument.second())
                                .executes(context ->
                                        teleport(context.getSource(), EntityArgumentType.getPlayers(context, "target"), RegionFirstArgument.getFirst(context, "first"), RegionSecondArgument.getSecond(context, "second"))))));
        command.then(tp);
        dispatcher.register(command);
    }

    private static int execute(ServerCommandSource source, @Nullable String first, @Nullable String second) throws CommandSyntaxException {
        var world = source.getWorld();
        var player = source.getPlayerOrThrow();
        var worldData = WorldData.get(world);
        var data = worldData.getChunkData(player.getBlockPos());
        var f = data.getFirst();
        var s = data.getSecond();
        if (first == null && second == null) {
            source.sendFeedback(() -> {
                var text = literal(world.getRegistryKey().getValue().toString());
                text.append(" 월드 청크");
                text.append(data.toString());
                if (f == null || s == null) {
                    return text.append("는 지역이 정해져 있지 않습니다.");
                } else {
                    text.append("는 ");
                    text.append(literal(format("%s %s", f, s)).styled(style -> style.withUnderline(true)));
                    text.append(literal("입니다.").styled(style -> style.withUnderline(false)));
                    return text;
                }
            }, false);
            return 0;
        }
        if (Objects.equals(first, RegionFirstArgument.DELETE)) {
            data.deleteRegion();
            var text = literal(world.getRegistryKey().getValue().toString());
            text.append(" 월드 청크");
            text.append(data.toString());
            text.append("에 지역 값이 제거되었습니다.");
            source.sendFeedback(() -> text, true);
            return 0;
        }
        if (first == null || second == null) {
            source.sendFeedback(() -> literal("first와 second의 값은 모두 있어야 합니다.").styled(style -> style.withColor(Formatting.RED)), false);
            return 1;
        }
        if (Objects.equals(f, first) && Objects.equals(s, second)) {
            var text = literal(world.getRegistryKey().getValue().toString()).styled(style -> style.withColor(Formatting.RED));
            text.append(" 월드 청크");
            text.append(data.toString());
            text.append("에 변경된 사항이 없습니다.");
            source.sendFeedback(() -> text, false);
        } else {
            data.setRegion(first, second);
            var text = literal(world.getRegistryKey().getValue().toString());
            text.append(" 월드 청크");
            text.append(data.toString());
            text.append(" 지역이 ");
            text.append(format("%s %s", first, second));
            text.append("으로 업데이트 되었습니다.");
            source.sendFeedback(() -> text, true);
        }
        return 0;
    }

    private static int teleport(ServerCommandSource source, @NotNull Collection<ServerPlayerEntity> targets, @NotNull String first, @NotNull String second) {
        var world = source.getWorld();
        var worldData = WorldData.get(world);
        var root = worldData.getRegionRoot(first, second);
        if (root == null) {
            source.sendError(literal("해당 구역은 대표 청크가 정해져 있지 않습니다."));
        } else {
            for (var target : targets) {
                root.teleport(target);
            }
            var players = targets.size() == 1 ? targets.iterator().next().getDisplayName() : literal(targets.size() + "명의 플레이어");
            var text = literal("");
            text.append(players);
            text.append("이(가) ");
            text.append(format("%s %s", first, second));
            text.append("으로 순간이동 되었습니다.");
            source.sendFeedback(() -> text, true);
        }
        return 0;
    }
}
