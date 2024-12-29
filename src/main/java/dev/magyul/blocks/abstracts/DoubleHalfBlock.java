package dev.magyul.blocks.abstracts;

import dev.magyul.util.MathHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class DoubleHalfBlock extends Block {
    public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;

    public DoubleHalfBlock(Settings settings) {
        super(settings);
        setDefaultState(stateManager.getDefaultState().with(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        DoubleBlockHalf half = state.get(HALF);
        if (direction.getAxis() == Direction.Axis.Y && half == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
            return neighborState.getBlock() instanceof DoubleHalfBlock && neighborState.get(HALF) != half ? neighborState.with(HALF, half) : Blocks.AIR.getDefaultState();
        } else {
            return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);//half == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() :
        }
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);
        return state.get(HALF) == DoubleBlockHalf.LOWER || blockState.isOf(this);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HALF);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        World world = ctx.getWorld();
        if (blockPos.getY() < world.getTopY() - 1 && world.getBlockState(blockPos.up()).canReplace(ctx)) {
            return this.getDefaultState().with(HALF, DoubleBlockHalf.LOWER);
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


    public static class HorizontalFacing extends DoubleHalfBlock {
        public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

        public HorizontalFacing(Settings settings) {
            super(settings);
        }

        @Override
        protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
            super.appendProperties(builder);
            builder.add(FACING);
        }

        @Override
        public BlockState getPlacementState(ItemPlacementContext ctx) {
            var state = super.getPlacementState(ctx);
            if (state != null) {
                return state.with(FACING, ctx.getHorizontalPlayerFacing());
            }
            return null;
        }

        public BlockState rotate(BlockState state, BlockRotation rotation) {
            return state.with(FACING, rotation.rotate(state.get(FACING)));
        }

        public BlockState mirror(BlockState state, BlockMirror mirror) {
            return state.rotate(mirror.getRotation(state.get(FACING)));
        }
    }
}
