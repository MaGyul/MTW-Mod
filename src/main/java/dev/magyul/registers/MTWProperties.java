package dev.magyul.registers;

import dev.magyul.blocks.enums.BlockLR;
import dev.magyul.blocks.enums.JuchoShape;
import dev.magyul.blocks.enums.TripleBlockHalf;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;

public class MTWProperties {
    public static final EnumProperty<TripleBlockHalf> TRIPLE_BLOCK_HALF = EnumProperty.of("half", TripleBlockHalf.class);
    public static final EnumProperty<BlockLR> BLOCK_LR = EnumProperty.of("lr", BlockLR.class);
    public static final EnumProperty<JuchoShape> JUCHO_SHAPE = EnumProperty.of("shape", JuchoShape.class);

    public static final BooleanProperty CENTER = BooleanProperty.of("center");
}
