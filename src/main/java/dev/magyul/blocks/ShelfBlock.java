package dev.magyul.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class ShelfBlock extends HorizontalFacingBlock {
    public static final MapCodec<ShelfBlock> CODEC = createCodec(ShelfBlock::new);
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

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case EAST -> EAST;
            case WEST -> WEST;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            default -> VoxelShapes.empty();
        };
    }
}
