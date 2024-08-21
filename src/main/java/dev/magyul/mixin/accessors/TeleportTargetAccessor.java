package dev.magyul.mixin.accessors;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TeleportTarget.class)
public interface TeleportTargetAccessor {
    @Mutable
    @Accessor
    void setWorld(ServerWorld world);

    @Mutable
    @Accessor
    void setPos(Vec3d pos);

    @Mutable
    @Accessor
    void setYaw(float yaw);

    @Mutable
    @Accessor
    void setPitch(float pitch);
}
