package dev.magyul.registers;

import dev.magyul.MTWMod;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class MTWTags {
    public static TagKey<Block> EARTHS = register("earths");
    public static TagKey<Block> PLATES = register("plates");
    public static TagKey<Block> WALLS = register("walls");
    public static TagKey<Block> CONNECTED_WALLS = register("connected_walls");
    public static TagKey<Block> CARRY_ON = register("carry_on");

    @SuppressWarnings("SameParameterValue")
    private static TagKey<Block> register(String id) {
        return TagKey.of(RegistryKeys.BLOCK, new Identifier(MTWMod.ID, id));
    }

    public static void init() {

    }
}
