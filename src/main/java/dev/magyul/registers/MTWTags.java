package dev.magyul.registers;

import dev.magyul.MTWMod;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class MTWTags {
    public static final TagKey<Block> EARTHS = registerBlock("earths");
    public static final TagKey<Block> PLATES = registerBlock("plates");
    public static final TagKey<Block> WALLS = registerBlock("walls");
    public static final TagKey<Block> CONNECTED_WALLS = registerBlock("connected_walls");
    public static final TagKey<Block> SITTINGS = registerBlock("sittings");
    public static final TagKey<Item> LANTERNS = registerItem("lanterns");

    private static TagKey<Block> registerBlock(String id) {
        return TagKey.of(RegistryKeys.BLOCK, new Identifier(MTWMod.ID, id));
    }

    private static TagKey<Item> registerItem(String id) {
        return TagKey.of(RegistryKeys.ITEM, new Identifier(MTWMod.ID, id));
    }

    public static void init() {

    }
}
