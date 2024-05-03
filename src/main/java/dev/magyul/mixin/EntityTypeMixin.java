package dev.magyul.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityType.class)
public class EntityTypeMixin {
    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityType$Builder;create(Lnet/minecraft/entity/EntityType$EntityFactory;Lnet/minecraft/entity/SpawnGroup;)Lnet/minecraft/entity/EntityType$Builder;", ordinal = 58))
    private static <T extends Entity> EntityType.Builder<T> ITEM(EntityType.EntityFactory<T> factory, SpawnGroup spawnGroup) {
        var builder = EntityType.Builder.create(factory, spawnGroup).dimensions(0.25F, 0.25F).eyeHeight(0.2125F).maxTrackingRange(6).trackingTickInterval(20);

        builder.dimensions = builder.dimensions.scaled(5);

        return builder;
    }
}
