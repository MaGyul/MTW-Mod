package dev.magyul.blocks;

import com.mojang.serialization.MapCodec;
import dev.magyul.util.MathHelper;
import net.minecraft.block.*;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.stream.Stream;

public class BlastFurnace extends HorizontalFacingBlock {
    public static final MapCodec<BlastFurnace> CODEC = createCodec(BlastFurnace::new);
    public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;

    public BlastFurnace(Settings settings) {
        super(settings);
        setDefaultState(stateManager.getDefaultState().with(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        var half = state.get(HALF);
        return half == DoubleBlockHalf.UPPER ? Shape.UPPER_SHAPE : Shape.LOWER_SHAPE;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        DoubleBlockHalf half = state.get(HALF);
        if (direction.getAxis() == Direction.Axis.Y && half == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
            return neighborState.getBlock() instanceof BlastFurnace && neighborState.get(HALF) != half ? neighborState.with(HALF, half) : Blocks.AIR.getDefaultState();
        } else {
            return half == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
        }
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);
        return state.get(HALF) == DoubleBlockHalf.LOWER ? blockState.isSideSolidFullSquare(world, blockPos, Direction.UP) : blockState.isOf(this);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HALF, FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        World world = ctx.getWorld();
        if (blockPos.getY() < world.getTopY() - 1 && world.getBlockState(blockPos.up()).canReplace(ctx)) {
            return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing()).with(HALF, DoubleBlockHalf.LOWER);
        }
        return null;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        world.setBlockState(pos.up(), state.with(HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Override
    public long getRenderingSeed(BlockState state, BlockPos pos) {
        return MathHelper.hashCode(pos.getX(), pos.down(state.get(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), pos.getZ());
    }

    private static class Shape {
        protected static final VoxelShape UPPER_SHAPE = Stream.of(
                createCuboidShape(3, 0, 2, 13, 16, 3),
                createCuboidShape(3, 0, 1, 13, 11, 2),
                createCuboidShape(13, 0, 3, 14, 16, 13),
                createCuboidShape(13, 0, 13, 14, 11, 14),
                createCuboidShape(2, 0, 13, 3, 11, 14),
                createCuboidShape(2, 0, 2, 3, 11, 3),
                createCuboidShape(13, 0, 2, 14, 11, 3),
                createCuboidShape(14, 0, 3, 15, 11, 13),
                createCuboidShape(1, 0, 3, 2, 11, 13),
                createCuboidShape(2, 0, 3, 3, 16, 13),
                createCuboidShape(3, 0, 13, 13, 16, 14),
                createCuboidShape(3, 0, 14, 13, 11, 15)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
        protected static final VoxelShape LOWER_SHAPE = Stream.of(
                Block.createCuboidShape(0, 0, 2, 14, 2, 14),
                Block.createCuboidShape(0, 6, 0, 16, 16, 2),
                Block.createCuboidShape(14, 0, 2, 16, 16, 14),
                Block.createCuboidShape(0, 0, 2, 2, 16, 14),
                Block.createCuboidShape(-2, 0, 1, 0, 12, 15),
                Block.createCuboidShape(16, 0, 1, 18, 12, 15),
                Block.createCuboidShape(1, 0, -2, 15, 12, 0),
                Block.createCuboidShape(1, 0, 16, 15, 12, 18),
                Block.createCuboidShape(9, 4, 0, 16, 6, 2),
                Block.createCuboidShape(0, 4, 0, 7, 6, 2),
                Block.createCuboidShape(0, 0, 0, 16, 4, 2),
                Block.createCuboidShape(0, 0, 14, 16, 6, 16),
                Block.createCuboidShape(0, 6, 14, 7, 8, 16),
                Block.createCuboidShape(9, 6, 14, 16, 8, 16),
                Block.createCuboidShape(0, 8, 14, 16, 16, 16),
                Block.createCuboidShape(-2, 0, 1, 0, 12, 15)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
    }
}
