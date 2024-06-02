package dev.magyul.blocks;

import dev.magyul.blocks.abstracts.DoubleHalfBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class BlastFurnace extends DoubleHalfBlock.HorizontalFacing {
    public BlastFurnace(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        var half = state.get(HALF);
        return half == DoubleBlockHalf.UPPER ? Shapes.BlastFurnace.UPPER_SHAPE : Shapes.BlastFurnace.LOWER_SHAPE;
    }
}
