package dev.magyul.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ItemUtil {

    @Nullable
    public static ItemEntity raycastItem(Entity camera, float reach) {
        var normalizedFacing = camera.getRotationVec(1.0F);
        var denormalizedFacing = camera.getCameraPosVec(0)
                .add(normalizedFacing.x * reach, normalizedFacing.y * reach, normalizedFacing.z * reach);

        final var result = ProjectileUtil.raycast(camera, camera.getCameraPosVec(0), denormalizedFacing,
                camera.getBoundingBox().stretch(normalizedFacing.multiply(reach)).expand(1),
                entity -> entity instanceof ItemEntity, reach * reach);

        if (result != null) {
            var distance = camera.getPos().distanceTo(result.getPos()) - .3;
            if (camera.raycast(distance, 1f, false) instanceof BlockHitResult blockResult) {
                var world = camera.getWorld();
                var blockPos = blockResult.getBlockPos();
                if (!world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty()) {
                    return null;
                }
            }
        }

        return result == null ? null : (ItemEntity) result.getEntity();
    }
}
