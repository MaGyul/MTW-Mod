package dev.magyul.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityType.class)
public class EntityTypeMixin {
    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityType$Builder;create(Lnet/minecraft/entity/EntityType$EntityFactory;Lnet/minecraft/entity/SpawnGroup;)Lnet/minecraft/entity/EntityType$Builder;", ordinal = 55))
    private static <T extends Entity> EntityType.Builder<T> ITEM(EntityType.EntityFactory<T> factory, SpawnGroup spawnGroup) {
        return EntityType.Builder.create(factory, spawnGroup).setDimensions(1.0F, 1.0F).maxTrackingRange(6).trackingTickInterval(20);
    }
}
