package dev.magyul.registers;

import dev.magyul.MTWMod;
import dev.magyul.blocks.AnvilBlock;
import dev.magyul.blocks.WallBlock;
import dev.magyul.blocks.*;
import net.minecraft.block.*;
import net.minecraft.block.enums.Instrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import static net.minecraft.block.Blocks.*;

public class MTWBlocks {
    public static final Block ERROR_BLOCK = register("error_block", new ErrorBlock(AbstractBlock.Settings.create()
            .mapColor(MapColor.STONE_GRAY)
            .instrument(Instrument.BASEDRUM)
            .strength(-1.0F, 3600000.0F)
            .dropsNothing()
            .luminance(ErrorBlock.LIGHT_EMISSION)
            .allowsSpawning((a, b, c, d) -> false)));
//    public static final Block OAK_JAPANESE_DOOR = register("oak_japanese_door", new JapaneseDoors(BlockSetType.OAK, AbstractBlock.Settings.create()
//            .mapColor(MapColor.OAK_TAN).nonOpaque().strength(1.5f, 1.0f).sounds(BlockSoundGroup.SCAFFOLDING)));
    // Full Cube
    public static final Block LINOLEUM_BRICKS = register("linoleum_bricks", new StairTypeBlock(AbstractBlock.Settings.create()
            .mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F)));
    // Transparent Full Cube
    public static final Block ANVIL = register("anvil", new AnvilBlock(AbstractBlock.Settings.create()
            .mapColor(MapColor.IRON_GRAY).requiresTool().strength(5.0F, 1200.0F).sounds(BlockSoundGroup.ANVIL).pistonBehavior(PistonBehavior.BLOCK).nonOpaque()));

    // 다양한 모양인 블록
    public static final Block SPRUCE_SIGNBOARD = register("spruce_signboard", new SignBoard(WoodType.SPRUCE, AbstractBlock.Settings.create()
            .mapColor(DARK_OAK_PLANKS.getDefaultMapColor()).instrument(Instrument.BASEDRUM).requiresTool().strength(1.4F).pistonBehavior(PistonBehavior.PUSH_ONLY)));
    public static final Block SPRUCE_JUCHO_STONE = register("spruce_jucho_stone", new JuchoStoneBlock(AbstractBlock.Settings.create()
            .mapColor(DARK_OAK_PLANKS.getDefaultMapColor()).instrument(Instrument.BASS).requiresTool().strength(1.5F, 6.0F).sounds(BlockSoundGroup.WOOD)));
    public static final Block SPRUCE_SHELF = register("spruce_shelf", new ShelfBlock(AbstractBlock.Settings.create()
            .mapColor(MapColor.SPRUCE_BROWN).instrument(Instrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable()));
    public static final Block BLAST_FURNACE = register("blast_furnace", new BlastFurnace(AbstractBlock.Settings.create()
            .mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASS).instrument(Instrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F)));

    // 얇은
    public static final Block SPRUCE_LARGE_HALL = register("spruce_large_hall", new MoreSlabBlock(AbstractBlock.Settings.create()
            .mapColor(MapColor.SPRUCE_BROWN).instrument(Instrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable()));
    public static final Block SPRUCE_RAFTERS = register("spruce_rafters", new MoreSlabBlock(AbstractBlock.Settings.create()
            .mapColor(MapColor.SPRUCE_BROWN).instrument(Instrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque().burnable()));
    public static final Block SPRUCE_LINOLEUM = register("spruce_linoleum", new MoreSlabBlock(AbstractBlock.Settings.create()
            .mapColor(MapColor.SPRUCE_BROWN).instrument(Instrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable()));
    public static final Block SPRUCE_LARGE_HALL_LINOLEUM = register("spruce_large_hall_linoleum", new MoreSlabStairBlock(AbstractBlock.Settings.create()
            .mapColor(MapColor.SPRUCE_BROWN).instrument(Instrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable()));

    // 울타리
    public static final Block EARTH_WALL = register("earth_wall", new WallBlock(AbstractBlock.Settings.create()
            .mapColor(OAK_PLANKS.getDefaultMapColor()).solid().instrument(Instrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable()));
    public static final Block SPRUCE_PLATE_WALL = register("spruce_plate_wall", new WallBlock(AbstractBlock.Settings.create()
            .mapColor(SPRUCE_PLANKS.getDefaultMapColor()).solid().instrument(Instrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable()));
    public static final Block SPRUCE_EARTH_WALL_PILLAR = register("spruce_earth_wall_pillar", new WallBlock(AbstractBlock.Settings.create()
            .mapColor(SPRUCE_PLANKS.getDefaultMapColor()).solid().instrument(Instrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable()));
    public static final Block SPRUCE_PLATE_WALL_PILLAR = register("spruce_plate_wall_pillar", new WallBlock(AbstractBlock.Settings.create()
            .mapColor(SPRUCE_PLANKS.getDefaultMapColor()).solid().instrument(Instrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable()));

    // Facing
    public static final Block SPRUCE_EARTH_WALL_FRAME = register("spruce_earth_wall_frame", new EarthBlock(AbstractBlock.Settings.create()
            .mapColor(DyeColor.WHITE).instrument(Instrument.BASEDRUM).requiresTool().strength(1.4F).pistonBehavior(PistonBehavior.PUSH_ONLY)));
    public static final Block SPRUCE_PLATE_WALL_FRAME = register("spruce_plate_wall_frame", new EarthBlock(AbstractBlock.Settings.create()
            .mapColor(DARK_OAK_PLANKS.getDefaultMapColor()).instrument(Instrument.BASEDRUM).requiresTool().strength(1.4F).pistonBehavior(PistonBehavior.PUSH_ONLY)));

    // 창문
    public static final Block OAK_KOREAN_PAPER_WINDOW = register("oak_korean_paper_window", new WindowBlock(WoodType.OAK, AbstractBlock.Settings.create()
            .mapColor(MapColor.OAK_TAN).instrument(Instrument.BASS).strength(3.0F).nonOpaque().allowsSpawning(Blocks::never).burnable()));

    // 문
    public static final Block OAK_KOREAN_PAPER_DOOR = register("oak_korean_paper_door", new BigDoorBlock(BlockSetType.OAK, AbstractBlock.Settings.create()
            .mapColor(OAK_PLANKS.getDefaultMapColor()).instrument(Instrument.BASS).strength(3.0F).nonOpaque().burnable().pistonBehavior(PistonBehavior.DESTROY)));
    public static final Block SPRUCE_PLATE_DOOR = register("spruce_plate_door", new BigDoorBlock(BlockSetType.SPRUCE, AbstractBlock.Settings.create()
            .mapColor(SPRUCE_PLANKS.getDefaultMapColor()).instrument(Instrument.BASS).strength(3.0F).nonOpaque().burnable().pistonBehavior(PistonBehavior.DESTROY)));

    private static Block register(String name, Block block) {
//        OAK_STAIRS
//        ChestBl
//        Blocks.OAK_LOG
        return Registry.register(Registries.BLOCK, new Identifier(MTWMod.ID, name), block);
    }

    public static void init() {
    }
}
