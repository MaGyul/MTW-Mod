package dev.magyul.entities;

import dev.magyul.registers.MTWEntityType;
import dev.magyul.util.SitUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SitEntity extends Entity {
    public SitEntity(EntityType<? extends SitEntity> type, World world) {
        super(type, world);
    }

    public SitEntity(World world) {
        super(MTWEntityType.SIT, world);
        noClip = true;
    }

    @Override
    public Vec3d updatePassengerForDismount(LivingEntity passenger) {
        if (passenger instanceof PlayerEntity player) {
            var resetPosition = SitUtil.getPreviousPlayerPosition(player, this);
            if (resetPosition != null) {
                discard();
                return resetPosition;
            }
        }

        discard();
        return super.updatePassengerForDismount(passenger);
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        SitUtil.removeSitEntity(getWorld(), getBlockPos());
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
    }
}
