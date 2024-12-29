package dev.magyul.registers;

import dev.magyul.MTWMod;
import dev.magyul.entities.SitEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class MTWEntityType {
    public static final EntityType<SitEntity> SIT = register("sit", FabricEntityTypeBuilder.<SitEntity>create(SpawnGroup.MISC, SitEntity::new).dimensions(EntityDimensions.changing(.001F, .001F)));

    private static <T extends Entity> EntityType<T> register(String id, FabricEntityTypeBuilder<T> builder) {
        return Registry.register(Registries.ENTITY_TYPE, Identifier.of(MTWMod.ID, id), builder.build());
    }

    public static void init() {

    }
}
