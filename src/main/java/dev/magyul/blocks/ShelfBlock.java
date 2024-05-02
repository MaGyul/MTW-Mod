package dev.magyul.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

@SuppressWarnings("deprecation")
public class ShelfBlock extends HorizontalFacingBlock {
    public static final MapCodec<ShelfBlock> CODEC = createCodec(ShelfBlock::new);
//    private static final VoxelShape X_AXIS_SHAPE = Block.createCuboidShape(12, 0, 0, 16, 16, 16);
//    private static final VoxelShape Z_AXIS_SHAPE = Block.createCuboidShape(0, 0, 12, 16, 16, 16);
    protected static final VoxelShape EAST = Block.createCuboidShape(
            12, 0, 0, 20, 16, 16);
    protected static final VoxelShape WEST = Block.createCuboidShape(
            -4, 0, 0, 4, 16, 16);
    protected static final VoxelShape SOUTH = Block.createCuboidShape(
            0, 0, 12, 16, 16, 20);
    protected static final VoxelShape NORTH = Block.createCuboidShape(
            0, 0, -4, 16, 16, 4);
    public ShelfBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case EAST -> EAST;
            case WEST -> WEST;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            default -> VoxelShapes.empty();
        };
//        Direction direction = state.get(FACING);
//        return direction.getAxis() == Direction.Axis.X ? X_AXIS_SHAPE : Z_AXIS_SHAPE;
    }
}
