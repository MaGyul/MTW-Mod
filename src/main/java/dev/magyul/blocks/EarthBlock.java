package dev.magyul.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class EarthBlock extends HorizontalFacingBlock {
    public static final MapCodec<EarthBlock> CODEC = createCodec(EarthBlock::new);
    private static final VoxelShape X_AXIS_SHAPE = Block.createCuboidShape(5, 0, 0, 11, 16, 16);
    private static final VoxelShape Z_AXIS_SHAPE = Block.createCuboidShape(0, 0, 5, 16, 16, 11);

    @Override
    public MapCodec<EarthBlock> getCodec() {
        return CODEC;
    }

    public EarthBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction direction = state.get(FACING);
        return direction.getAxis() == Direction.Axis.X ? X_AXIS_SHAPE : Z_AXIS_SHAPE;
    }
}
