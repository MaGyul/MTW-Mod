package dev.magyul.util;

import dev.magyul.blocks.entities.SignBoardEntity;
import dev.magyul.data.PlayerData;
import dev.magyul.network.SignBoardEditorOpenS2CPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Unique;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ServerUtil {
    public static final DoubleKeyMap<UUID, Integer, Boolean> playerKey = new DoubleKeyMap<>();
    public static Consumer<SignBoardEntity> clientCallSignBoardEntity;
    public static final int modInfoPort = 27767;
    public static AtomicBoolean allowLogins = new AtomicBoolean(false);

    private static final Set<Property<?>> PROPERTY_EXCEPTION_CLASSES = new HashSet<>();

    static {
        String[] placementStateExceptions = {
                "minecraft:chest[type]",
                "minecraft:stone_button[face]",
                "minecraft:vine[north,east,south,west,up]",
                "minecraft:creeper_head[rotation]",
                "minecraft:glow_lichen[north,east,south,west,up,down]",
                "minecraft:oak_sign[rotation]",
                "minecraft:oak_trapdoor[half]",
        };
        for (String propString : placementStateExceptions) {
            if (!propString.contains("[") || !propString.contains("]"))
                continue;
            String name = propString.substring(0, propString.indexOf("["));
            String props = propString.substring(propString.indexOf("[") + 1, propString.indexOf("]"));
            Block blk = Registries.BLOCK.get(new Identifier(name));
            for (String propName : props.split(",")) {
                for (Property<?> prop : blk.getDefaultState().getProperties()) {
                    if (prop.getName().equals(propName))
                        PROPERTY_EXCEPTION_CLASSES.add(prop);
                }
            }
        }
    }

    public static void openSignBoardEditor(PlayerEntity player, SignBoardEntity boardEntity) {
        boardEntity.setEditor(player.getUuid());
        if (player instanceof ServerPlayerEntity serverPlayer) {
            ServerPlayNetworking.send(serverPlayer, new SignBoardEditorOpenS2CPacket(boardEntity.getPos()));
        } else {
            clientCallSignBoardEntity.accept(boardEntity);
        }
    }

    public static boolean isKeyDown(@NotNull PlayerEntity player, int key) {
        var map = playerKey.get(player.getUuid());
        if (map == null) return false;
        if (!map.containsKey(key)) return false;
        return map.get(key);
    }

    public static Vec3d getDirectionPos(ServerPlayerEntity player) {
        var rotX = player.getYaw();
        var rotY = player.getPitch();

        var xz = Math.cos(Math.toRadians(rotY));

        return new Vec3d(
                -xz * Math.sin(Math.toRadians(rotX)),
                -Math.sin(Math.toRadians(rotY)),
                xz * Math.cos(Math.toRadians(rotX)));
    }

    public static boolean canCarryGeneral(ServerPlayerEntity player, Vec3d pos) {
        if (!player.getMainHandStack().isEmpty() || !player.getOffHandStack().isEmpty()) return false;
        if (player.getPos().distanceTo(pos) > 5) return false;
        if (PlayerData.hasCarryState(player)) return false;
        return player.age != PlayerData.lastTick(player);
    }

    public static BlockPos getDeathPlacementPos(BlockState state, ServerPlayerEntity player) {
        BlockPos p = player.getBlockPos();

        int DISTANCE = 15;

        List<BlockPos> potentialPositions = new ArrayList<>();

        for (int j = 0; j < DISTANCE * 2; j++) {
            for (int i = 0; i < DISTANCE * 2; i++) {
                for (int k = 0; k < DISTANCE * 2; k++) {
                    int x = i % 2 == 0 ? i / 2 : -(i / 2);
                    int y = j % 2 == 0 ? j / 2 : -(j / 2);
                    int z = k % 2 == 0 ? k / 2 : -(k / 2);
                    potentialPositions.add(new BlockPos(p.getX() + x, p.getY() + y, p.getZ() + z));
                }
            }
        }

        potentialPositions.sort(Comparator.comparingDouble(posA -> player.squaredDistanceTo(posA.toCenterPos())));

        for (BlockPos potential : potentialPositions) {
            ItemPlacementContext context = new ItemPlacementContext(player, Hand.MAIN_HAND, ItemStack.EMPTY, BlockHitResult.createMissed(Vec3d.ofCenter(potential), Direction.DOWN, potential));
            boolean canPlace = state.canPlaceAt(player.getWorld(), potential) && player.getWorld().getBlockState(potential).canReplace(context) && player.getWorld().canPlace(state, potential, ShapeContext.of(player));

            if (canPlace)
                return potential;
        }

        return p;
    }

    public static BlockState getPlacementState(BlockState state, ServerPlayerEntity player, ItemPlacementContext context, BlockPos pos) {
        BlockState placementState = state.getBlock().getPlacementState(context);
        if (placementState == null || placementState.getBlock() != state.getBlock())
            placementState = state;

        for (var prop : placementState.getProperties()) {
            if (prop instanceof DirectionProperty)
                state = updateProperty(state, placementState, prop);
            if (prop.getType() == Direction.Axis.class)
                state = updateProperty(state, placementState, prop);
            //This is needed for certain blocks, otherwise we get problems like chests not connecting
            if (PROPERTY_EXCEPTION_CLASSES.contains(prop)) {
                state = updateProperty(state, placementState, prop);
            }
        }

        BlockState updatedState = Block.postProcessState(state, player.getWorld(), pos);
        if (updatedState.getBlock() == state.getBlock())
            state = updatedState;

        if (placementState.contains(Properties.WATERLOGGED) && state.contains(Properties.WATERLOGGED))
            state = state.with(Properties.WATERLOGGED, placementState.get(Properties.WATERLOGGED));

        return state;
    }

    private static <T extends Comparable<T>> BlockState updateProperty(BlockState state, BlockState otherState, Property<T> prop) {
        var val = otherState.get(prop);
        return state.with(prop, val);
    }

    public static void sleep(long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ignored) {
        }
    }

    public static Timer setTimeout(Runnable run, long delay) {
        var timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                run.run();
            }
        }, delay);
        return timer;
    }

    public static Timer setInterval(Runnable run, long period) {
        var timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                run.run();
            }
        }, period, period);
        return timer;
    }

    public static Timer setInterval(Runnable run, long delay, long period) {
        var timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                run.run();
            }
        }, delay, period);
        return timer;
    }

    public static void cancelTimer(Timer timer) {
        if (timer != null) timer.cancel();
    }
}
