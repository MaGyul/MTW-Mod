package dev.magyul.registers;

import dev.magyul.blocks.enums.BlockLR;
import dev.magyul.blocks.enums.JuchoShape;
import dev.magyul.blocks.enums.SideWall;
import dev.magyul.blocks.enums.TripleBlockHalf;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;

public class MTWProperties {
    public static final EnumProperty<TripleBlockHalf> TRIPLE_BLOCK_HALF = EnumProperty.of("half", TripleBlockHalf.class);
    public static final EnumProperty<BlockLR> BLOCK_LR = EnumProperty.of("lr", BlockLR.class);
    public static final EnumProperty<JuchoShape> JUCHO_SHAPE = EnumProperty.of("shape", JuchoShape.class);
    public static final EnumProperty<SideWall> SIDE_WALL_NORTH = EnumProperty.of("north", SideWall.class);
    public static final EnumProperty<SideWall> SIDE_WALL_EAST = EnumProperty.of("east", SideWall.class);
    public static final EnumProperty<SideWall> SIDE_WALL_SOUTH = EnumProperty.of("south", SideWall.class);
    public static final EnumProperty<SideWall> SIDE_WALL_WEST = EnumProperty.of("west", SideWall.class);

    public static final BooleanProperty CENTER = BooleanProperty.of("center");

}
