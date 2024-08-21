package dev.magyul.registers;

import dev.magyul.MTWMod;
import dev.magyul.blocks.AnvilBlock;
import dev.magyul.blocks.SideWallBlock;
import dev.magyul.blocks.*;
import dev.magyul.blocks.WallBlock;
import net.minecraft.block.*;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;

import static net.minecraft.block.Blocks.*;

public class MTWBlocks {
    public static final List<Block> MTW_BLOCKS = new ArrayList<>();
    private static final ToIntFunction<BlockState> LIGHT_15 = (state) -> 15;

    // Full Cube
    public static final Block ERROR_BLOCK = register("error_block", new ErrorBlock(AbstractBlock.Settings.create()
            .mapColor(MapColor.STONE_GRAY)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .strength(-1.0F, 3600000.0F)
            .dropsNothing()
            .luminance(ErrorBlock.LIGHT_EMISSION)
            .allowsSpawning((a, b, c, d) -> false)));
    public static final Block LINOLEUM_BRICKS = register("linoleum_bricks", new StairTypeBlock(AbstractBlock.Settings.create()
            .mapColor(MapColor.STONE_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F)));
    // Transparent Full Cube
    public static final Block ANVIL = register("anvil", new AnvilBlock(AbstractBlock.Settings.create()
            .mapColor(MapColor.IRON_GRAY).requiresTool().strength(5.0F, 1200.0F).sounds(BlockSoundGroup.ANVIL).pistonBehavior(PistonBehavior.BLOCK).nonOpaque()));

    // 다양한 모양인 블록
    public static final Block SPRUCE_SIGNBOARD = register("spruce_signboard", new SignBoard(WoodType.SPRUCE, AbstractBlock.Settings.create()
            .mapColor(DARK_OAK_PLANKS.getDefaultMapColor()).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.4F).pistonBehavior(PistonBehavior.PUSH_ONLY)));
    public static final Block SPRUCE_JUCHO_STONE = register("spruce_jucho_stone", new JuchoStoneBlock(AbstractBlock.Settings.create()
            .mapColor(DARK_OAK_PLANKS.getDefaultMapColor()).instrument(NoteBlockInstrument.BASS).requiresTool().strength(1.5F, 6.0F).sounds(BlockSoundGroup.WOOD)));
    public static final Block SPRUCE_SHELF = register("spruce_shelf", new ShelfBlock(AbstractBlock.Settings.create()
            .mapColor(MapColor.SPRUCE_BROWN).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable()));
    public static final Block SANDSTONE_LANTERN = register("sandstone_lantern", new SSLantern(AbstractBlock.Settings.create()
            .mapColor(MapColor.PALE_YELLOW).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(.8F).luminance(LIGHT_15)));
    public static final Block BLAST_FURNACE = register("blast_furnace", new BlastFurnace(AbstractBlock.Settings.create()
            .mapColor(MapColor.STONE_GRAY).instrument(NoteBlockInstrument.BASS).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F)));
    public static final Block SPRUCE_PLANKS_CHAIR = register("spruce_planks_chair", new SprucePlanksChair(AbstractBlock.Settings.create()
            .mapColor(MapColor.SPRUCE_BROWN).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque().burnable()));
    public static final Block STANDARD = register("standard", new StandardBlock(AbstractBlock.Settings.create()
            .mapColor(MapColor.PALE_YELLOW).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).noCollision().burnable().luminance(StandardBlock.LUMINANCE)));
    public static final Block FOOD_TABLE = register("food_table", new FoodTableBlock(AbstractBlock.Settings.create()
            .mapColor(MapColor.PALE_YELLOW).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque().burnable()));
    public static final Block SPRUCE_TILE_CENTER = register("spruce_tile_center", new TileBlock(AbstractBlock.Settings.create()
            .mapColor(MapColor.SPRUCE_BROWN).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque().burnable()));
    public static final Block SPRUCE_HIGH_SLOPE_TILE = register("spruce_high_slope_tile", new TileStairBlock(AbstractBlock.Settings.create()
            .mapColor(MapColor.STONE_GRAY).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5F, 6.0F).sounds(BlockSoundGroup.STONE).nonOpaque()));
    public static final Block SPRUCE_LOW_SLOPE_TILE_BOTTOM = register("spruce_low_slope_tile_bottom", new TileStairBlock(AbstractBlock.Settings.create()
            .mapColor(MapColor.STONE_GRAY).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5F, 6.0F).sounds(BlockSoundGroup.STONE).nonOpaque()));
    public static final Block SPRUCE_LOW_SLOPE_TILE_TOP = register("spruce_low_slope_tile_top", new TileStairBlock(AbstractBlock.Settings.create()
            .mapColor(MapColor.STONE_GRAY).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5F, 6.0F).sounds(BlockSoundGroup.STONE).nonOpaque()));
    public static final Block SPRUCE_LOW_SLOPE_TILE_END = register("spruce_low_slope_tile_end", new TileStairBlock(AbstractBlock.Settings.create()
            .mapColor(MapColor.STONE_GRAY).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5F, 6.0F).sounds(BlockSoundGroup.STONE).nonOpaque()));

    // 얇은
    public static final Block SPRUCE_LARGE_HALL = register("spruce_large_hall", new MoreSlabBlock(AbstractBlock.Settings.create()
            .mapColor(MapColor.SPRUCE_BROWN).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable()));
    public static final Block SPRUCE_LINOLEUM = register("spruce_linoleum", new MoreSlabBlock(AbstractBlock.Settings.create()
            .mapColor(MapColor.SPRUCE_BROWN).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable()));
    public static final Block SPRUCE_LARGE_HALL_LINOLEUM = register("spruce_large_hall_linoleum", new MoreSlabStairBlock(AbstractBlock.Settings.create()
            .mapColor(MapColor.SPRUCE_BROWN).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable()));
    public static final Block SPRUCE_RAFTERS = register("spruce_rafters", new MoreSlabStairBlock(AbstractBlock.Settings.create()
            .mapColor(MapColor.SPRUCE_BROWN).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque().burnable()));

    // 울타리 형식 벽
    public static final Block SPRUCE_WALL_PILLAR = register("spruce_wall_pillar", new WallBlock(AbstractBlock.Settings.create()
            .mapColor(SPRUCE_PLANKS.getDefaultMapColor()).solid().instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable()));
    public static final Block EARTH_WALL = register("earth_wall", new SideWallBlock(AbstractBlock.Settings.create()
            .mapColor(OAK_PLANKS.getDefaultMapColor()).solid().instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable()));
    public static final Block SPRUCE_PLATE_WALL = register("spruce_plate_wall", new SideWallBlock(AbstractBlock.Settings.create()
            .mapColor(SPRUCE_PLANKS.getDefaultMapColor()).solid().instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable()));

    // 뱡향 있는 벽
    public static final Block SPRUCE_EARTH_WALL_FRAME = register("spruce_earth_wall_frame", new EarthBlock(AbstractBlock.Settings.create()
            .mapColor(DyeColor.WHITE).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.4F).pistonBehavior(PistonBehavior.PUSH_ONLY)));
    public static final Block SPRUCE_PLATE_WALL_FRAME = register("spruce_plate_wall_frame", new EarthBlock(AbstractBlock.Settings.create()
            .mapColor(DARK_OAK_PLANKS.getDefaultMapColor()).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.4F).pistonBehavior(PistonBehavior.PUSH_ONLY)));
    public static final Block SPRUCE_EARTH_WALL = register("spruce_earth_wall", new SpruceEarthWall(AbstractBlock.Settings.create()
            .mapColor(MapColor.SPRUCE_BROWN).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable().nonOpaque()));

    // 창문
    public static final Block SPRUCE_LATTICE_WINDOW = register("spruce_lattice_window", new SlabWindowBlock(WoodType.SPRUCE, AbstractBlock.Settings.create()
            .mapColor(MapColor.SPRUCE_BROWN).instrument(NoteBlockInstrument.BASS).strength(3.0F).nonOpaque().allowsSpawning(Blocks::never).burnable()));

    // 문
    public static final Block OAK_KOREAN_PAPER_WINDOW = register("oak_korean_paper_window", new WindowBlock(WoodType.OAK, AbstractBlock.Settings.create()
            .mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASS).strength(3.0F).nonOpaque().allowsSpawning(Blocks::never).burnable()));
    public static final Block OAK_KOREAN_PAPER_DOOR = register("oak_korean_paper_door", new BigDoorBlock(BlockSetType.OAK, AbstractBlock.Settings.create()
            .mapColor(OAK_PLANKS.getDefaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(3.0F).nonOpaque().burnable().pistonBehavior(PistonBehavior.DESTROY)));
    public static final Block OAK_KOREAN_PAPER_SLIDING_DOOR = register("oak_korean_paper_sliding_door", new BigDoorBlock(BlockSetType.OAK, AbstractBlock.Settings.create()
            .mapColor(OAK_PLANKS.getDefaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(3.0F).nonOpaque().burnable().pistonBehavior(PistonBehavior.DESTROY)));
    public static final Block SPRUCE_PLATE_DOOR = register("spruce_plate_door", new BigDoorBlock(BlockSetType.SPRUCE, AbstractBlock.Settings.create()
            .mapColor(SPRUCE_PLANKS.getDefaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(3.0F).nonOpaque().burnable().pistonBehavior(PistonBehavior.DESTROY)));

    private static Block register(String name, Block block) {
        MTW_BLOCKS.add(block);
//        OAK_STAIRS
//        ChestBl
//        Blocks.OAK_LOG
        return Registry.register(Registries.BLOCK, Identifier.of(MTWMod.ID, name), block);
    }

    public static void init() {
    }
}
