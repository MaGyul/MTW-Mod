package dev.magyul.world;

import dev.magyul.MTWMod;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class DevelopDimensions {
    public static final RegistryKey<World> DEVELOP_DIMENSION_KEY = RegistryKey.of(
            RegistryKeys.WORLD,
            new Identifier(MTWMod.ID, "develop")
    );
    public static final RegistryKey<DimensionType> DEVELOP_TYPE_KEY = RegistryKey.of(
            RegistryKeys.DIMENSION_TYPE,
            DEVELOP_DIMENSION_KEY.getValue()
    );

    public static void register() {
        MTWMod.LOGGER.debug("Registering DevelopDimensions for {}", MTWMod.ID);
    }
}
