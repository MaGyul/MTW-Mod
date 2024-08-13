package dev.magyul.util;

import dev.magyul.entities.SitEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class SitUtil {
    private static final Map<Identifier, Map<BlockPos, Pair<SitEntity, Vec3d>>> OCCUPIED = new HashMap<>();

    public static boolean addSitEntity(World world, BlockPos blockPos, SitEntity entity, Vec3d playerPos) {
        if (!world.isClient) {
            var id = getDimensionTypeId(world);
            OCCUPIED.computeIfAbsent(id, (unused) -> new HashMap<>());
            OCCUPIED.get(id).put(blockPos, Pair.of(entity, playerPos));
            return true;
        }
        return false;
    }

    public static boolean removeSitEntity(World world, BlockPos pos) {
        if (!world.isClient) {
            var id = getDimensionTypeId(world);
            if (OCCUPIED.containsKey(id)) {
                OCCUPIED.get(id).remove(pos);
                return true;
            }
        }
        return false;
    }

    @Nullable
    public static SitEntity getSitEntity(World world, BlockPos pos) {
        if (!world.isClient) {
            var id = getDimensionTypeId(world);
            if (OCCUPIED.containsKey(id) && OCCUPIED.get(id).containsKey(pos)) {
                return OCCUPIED.get(id).get(pos).getLeft();
            }
        }

        return null;
    }

    @Nullable
    public static Vec3d getPreviousPlayerPosition(PlayerEntity player, SitEntity sitEntity) {
        if (!player.getWorld().isClient) {
            var id = getDimensionTypeId(player.getWorld());
            if (OCCUPIED.containsKey(id)) {
                var values = OCCUPIED.get(id).values();
                for (Pair<SitEntity, Vec3d> pair : values) {
                    if (pair.getLeft() == sitEntity) {
                        return pair.getRight();
                    }
                }
            }
        }

        return null;
    }

    public static boolean isOccupied(World world, BlockPos pos) {
        var id = getDimensionTypeId(world);
        return OCCUPIED.containsKey(id) && OCCUPIED.get(id).containsKey(pos);
    }

    public static boolean isPlayerSitting(PlayerEntity player) {
        var entrySet = OCCUPIED.entrySet();

        for (Map.Entry<Identifier, Map<BlockPos, Pair<SitEntity, Vec3d>>> entry : entrySet) {
            var values = entry.getValue().values();

            for (Pair<SitEntity, Vec3d> pair : values) {
                if (pair.getLeft().hasPassenger(player)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static Identifier getDimensionTypeId(World world) {
        return world.getRegistryKey().getValue();
    }
}
