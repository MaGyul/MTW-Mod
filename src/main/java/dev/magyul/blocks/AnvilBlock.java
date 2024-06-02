package dev.magyul.blocks;

import com.mojang.serialization.MapCodec;
import dev.magyul.blocks.enums.TripleBlockHalf;
import dev.magyul.registers.MTWProperties;
import dev.magyul.util.DirectionUtil;
import dev.magyul.util.MathHelper;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class AnvilBlock extends HorizontalFacingBlock {
    public static final MapCodec<AnvilBlock> CODEC = createCodec(AnvilBlock::new);
    public static final EnumProperty<TripleBlockHalf> HALF = MTWProperties.TRIPLE_BLOCK_HALF;
    public AnvilBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        var half = state.get(HALF);
        var facing = state.get(FACING);
        if (half == TripleBlockHalf.TOP) {
            if (facing == Direction.SOUTH) {
                return Shapes.AnvilBlock.TOP_NORTH;
            } else if (facing == Direction.WEST) {
                return Shapes.AnvilBlock.TOP_EAST;
            } else if (facing == Direction.EAST) {
                return Shapes.AnvilBlock.TOP_WEST;
            } else {
                return Shapes.AnvilBlock.TOP_SOUTH;
            }
        } else if (half == TripleBlockHalf.UPPER) {
            if (facing == Direction.SOUTH) {
                return Shapes.AnvilBlock.UPPER_NORTH;
            } else if (facing == Direction.WEST) {
                return Shapes.AnvilBlock.UPPER_EAST;
            } else if (facing == Direction.EAST) {
                return Shapes.AnvilBlock.UPPER_WEST;
            } else {
                return Shapes.AnvilBlock.UPPER_SOUTH;
            }
        } else {
            if (facing == Direction.SOUTH) {
                return Shapes.AnvilBlock.LOWER_NORTH;
            } else if (facing == Direction.WEST) {
                return Shapes.AnvilBlock.LOWER_EAST;
            } else if (facing == Direction.EAST) {
                return Shapes.AnvilBlock.LOWER_WEST;
            } else {
                return Shapes.AnvilBlock.LOWER_SOUTH;
            }
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        var half = state.get(HALF);
        if (half == TripleBlockHalf.TOP) {
            if (direction == DirectionUtil.getRight(state.get(FACING))) {
                return neighborState.isOf(this) ? state : Blocks.AIR.getDefaultState();
            }
        } else if (half == TripleBlockHalf.LOWER) {
            if (direction == DirectionUtil.getLeft(state.get(FACING))) {
                return neighborState.isOf(this) ? state : Blocks.AIR.getDefaultState();
            }
        } else {
            if (direction == DirectionUtil.getLeft(state.get(FACING))) {
                return neighborState.isOf(this) ? state : Blocks.AIR.getDefaultState();
            }
            if (direction == DirectionUtil.getRight(state.get(FACING))) {
                return neighborState.isOf(this) ? state : Blocks.AIR.getDefaultState();
            }
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HALF, FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        var facing = ctx.getHorizontalPlayerFacing();
        var world = ctx.getWorld();
        var worldBorder = world.getWorldBorder();
        var pos = ctx.getBlockPos();
        var leftPos = pos.offset(DirectionUtil.getLeft(facing));
        var rightPos = pos.offset(DirectionUtil.getRight(facing));
        var leftState = world.getBlockState(leftPos);
        var rightState = world.getBlockState(rightPos);
        if (leftState.canReplace(ctx) && rightState.canReplace(ctx) && worldBorder.contains(leftPos) && worldBorder.contains(rightPos)) {
            return this.getDefaultState().with(FACING, facing).with(HALF, TripleBlockHalf.UPPER);
        }

        return null;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        var facing = state.get(FACING);
        world.setBlockState(pos.offset(DirectionUtil.getLeft(facing)), state.with(HALF, TripleBlockHalf.TOP), 3);
        world.setBlockState(pos.offset(DirectionUtil.getRight(facing)), state.with(HALF, TripleBlockHalf.LOWER), 3);
    }

    @Override
    public long getRenderingSeed(BlockState state, BlockPos pos) {
        var half = state.get(HALF);
        var facing = state.get(FACING);
        return switch (half) {
            case TOP -> MathHelper.hashCode(pos.offset(DirectionUtil.getLeft(facing)));
            case UPPER -> super.getRenderingSeed(state, pos);
            case LOWER -> MathHelper.hashCode(pos.offset(DirectionUtil.getRight(facing)));
        };
    }
}
