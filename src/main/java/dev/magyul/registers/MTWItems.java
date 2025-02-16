package dev.magyul.registers;

import dev.magyul.MTWMod;
import dev.magyul.api.MTWToolMaterials;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MTWItems {
    public static final Map<Type, List<Item>> ITEMMAP = new HashMap<>() {
        {
            for (var type : Type.values()) put(type, new ArrayList<>());
        }
    };
    public static final List<Item> ITEMS = new ArrayList<>();
    public static final Item MTW_ICON = register("mtw_icon", getFood());
    public static final Item MTW_REGION_VIEWER = register("mtw_region_viewer", getFood());
    public static final Item RING_SWORD = register("ring_sword", new SwordItem(MTWToolMaterials.RING, 5, -2.4F, new Item.Settings()));
    public static final Item INDEPENDENCE_DECLARATION = register("independence_declaration", new Item(new Item.Settings().maxCount(1)));

    // Full Cute
    public static final Item ERROR_BLOCK = register("error_block", MTWBlocks.ERROR_BLOCK, Type.NORMAL);
    public static final Item LINOLEUM_BRICKS = register("linoleum_bricks", MTWBlocks.LINOLEUM_BRICKS, Type.NORMAL);
    public static final Item GRASS_BLOCK_SIDE_R = register("grass_block_side_r", MTWBlocks.GRASS_BLOCK_SIDE_R, Type.NORMAL);

    // 다양한 모양인 블록
    public static final Item SPRUCE_JUCHO_STONE = register("spruce_jucho_stone", MTWBlocks.SPRUCE_JUCHO_STONE, Type.DIFFERENT_SHAPE);
    public static final Item SPRUCE_SIGNBOARD = register("spruce_signboard", MTWBlocks.SPRUCE_SIGNBOARD, Type.DIFFERENT_SHAPE);
    public static final Item SPRUCE_SHELF = register("spruce_shelf", MTWBlocks.SPRUCE_SHELF, Type.DIFFERENT_SHAPE);
    public static final Item ANVIL = register("anvil", MTWBlocks.ANVIL, Type.DIFFERENT_SHAPE);
    public static final Item SANDSTONE_LANTERN = register("sandstone_lantern", MTWBlocks.SANDSTONE_LANTERN, Type.DIFFERENT_SHAPE);
    public static final Item BLAST_FURNACE = register("blast_furnace", MTWBlocks.BLAST_FURNACE, Type.DIFFERENT_SHAPE);
    public static final Item SPRUCE_PLANKS_CHAIR = register("spruce_planks_chair", MTWBlocks.SPRUCE_PLANKS_CHAIR, Type.DIFFERENT_SHAPE);
    public static final Item STANDARD = register("standard", MTWBlocks.STANDARD, Type.DIFFERENT_SHAPE);
    public static final Item FOOD_TABLE = register("food_table", MTWBlocks.FOOD_TABLE, Type.DIFFERENT_SHAPE);
    public static final Item SPRUCE_TILE_CENTER = register("spruce_tile_center", MTWBlocks.SPRUCE_TILE_CENTER, Type.DIFFERENT_SHAPE);
    public static final Item SPRUCE_HIGH_SLOPE_TILE = register("spruce_high_slope_tile", MTWBlocks.SPRUCE_HIGH_SLOPE_TILE, Type.DIFFERENT_SHAPE);
    public static final Item SPRUCE_LOW_SLOPE_TILE_BOTTOM = register("spruce_low_slope_tile_bottom", MTWBlocks.SPRUCE_LOW_SLOPE_TILE_BOTTOM, Type.DIFFERENT_SHAPE);
    public static final Item SPRUCE_LOW_SLOPE_TILE_TOP = register("spruce_low_slope_tile_top", MTWBlocks.SPRUCE_LOW_SLOPE_TILE_TOP, Type.DIFFERENT_SHAPE);
    public static final Item SPRUCE_LOW_SLOPE_TILE_END = register("spruce_low_slope_tile_end", MTWBlocks.SPRUCE_LOW_SLOPE_TILE_END, Type.DIFFERENT_SHAPE);

    // 얇은
    public static final Item SPRUCE_LARGE_HALL = register("spruce_large_hall", MTWBlocks.SPRUCE_LARGE_HALL, Type.MORE_SLAB);
    public static final Item SPRUCE_LINOLEUM = register("spruce_linoleum", MTWBlocks.SPRUCE_LINOLEUM, Type.MORE_SLAB);
    public static final Item SPRUCE_LARGE_HALL_LINOLEUM = register("spruce_large_hall_linoleum", MTWBlocks.SPRUCE_LARGE_HALL_LINOLEUM, Type.MORE_SLAB);
    public static final Item SPRUCE_RAFTERS = register("spruce_rafters", MTWBlocks.SPRUCE_RAFTERS, Type.MORE_SLAB);

    // 울타리 형식 벽
    public static final Item SPRUCE_WALL_PILLAR = register("spruce_wall_pillar", MTWBlocks.SPRUCE_WALL_PILLAR, Type.WALL);
    public static final Item EARTH_WALL = register("earth_wall", MTWBlocks.EARTH_WALL, Type.WALL);
    public static final Item SPRUCE_PLATE_WALL = register("spruce_plate_wall", MTWBlocks.SPRUCE_PLATE_WALL, Type.WALL);

    // 뱡향 있는 벽
    public static final Item SPRUCE_EARTH_WALL_FRAME = register("spruce_earth_wall_frame", MTWBlocks.SPRUCE_EARTH_WALL_FRAME, Type.EARTH);
    public static final Item SPRUCE_PLATE_WALL_FRAME = register("spruce_plate_wall_frame", MTWBlocks.SPRUCE_PLATE_WALL_FRAME, Type.EARTH);
    public static final Item SPRUCE_EARTH_WALL = register("spruce_earth_wall", MTWBlocks.SPRUCE_EARTH_WALL, Type.EARTH);

    // 창문
    public static final Item SPRUCE_LATTICE_WINDOW = register("spruce_lattice_window", MTWBlocks.SPRUCE_LATTICE_WINDOW, Type.DOOR);

    // 문
    public static final Item OAK_KOREAN_PAPER_WINDOW = register("oak_korean_paper_window", MTWBlocks.OAK_KOREAN_PAPER_WINDOW, Type.DOOR);
    public static final Item OAK_KOREAN_PAPER_DOOR = register("oak_korean_paper_door", MTWBlocks.OAK_KOREAN_PAPER_DOOR, Type.DOOR);
    public static final Item OAK_KOREAN_PAPER_SLIDING_DOOR = register("oak_korean_paper_sliding_door", MTWBlocks.OAK_KOREAN_PAPER_SLIDING_DOOR, Type.DOOR);
    public static final Item SPRUCE_PLATE_DOOR = register("spruce_plate_door", MTWBlocks.SPRUCE_PLATE_DOOR, Type.DOOR);

    private static Item register(String name, Block block, Type type) {
        var item = register(name, new BlockItem(block, new Item.Settings()));
        ITEMMAP.get(type).add(item);
        return item;
    }

    private static Item register(String name, Item item) {
        try {
            return Registry.register(Registries.ITEM, Identifier.of(MTWMod.ID, name), item);
        } finally {
            ITEMS.add(item);
        }
    }

    private static void registerItemGroup() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
            for (var item : List.of(MTW_ICON, MTW_REGION_VIEWER)) {
                entries.add(item);
            }
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
            for (var item : ITEMS) {
                if (item instanceof BlockItem) {
                    entries.add(item);
                }
            }
        });
    }

    private static Item getFood() {
        return new Item(new Item.Settings()
                .food(new FoodComponent.Builder()
                    .alwaysEdible()
                    .hunger(2000000000)
                    .saturationModifier(Float.MAX_VALUE)
                    .snack()
                    .build()
                )
                .maxCount(99));
    }

    public static void init() {
        registerItemGroup();
    }

    public enum Type {
        NORMAL,
        DIFFERENT_SHAPE,
        MORE_SLAB,
        WALL,
        EARTH,
        DOOR
    }
}
