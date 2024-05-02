package dev.magyul.blocks;

import com.mojang.serialization.MapCodec;
import dev.magyul.blocks.enums.TripleBlockHalf;
import dev.magyul.registers.MTWProperties;
import dev.magyul.util.DirectionUtil;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.stream.Stream;

@SuppressWarnings("deprecation")
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

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        var half = state.get(HALF);
        var facing = state.get(FACING);
        if (half == TripleBlockHalf.TOP) {
            if (facing == Direction.SOUTH) {
                return Shape.TOP_NORTH;
            } else if (facing == Direction.WEST) {
                return Shape.TOP_EAST;
            } else if (facing == Direction.EAST) {
                return Shape.TOP_WEST;
            } else {
                return Shape.TOP_SOUTH;
            }
        } else if (half == TripleBlockHalf.UPPER) {
            if (facing == Direction.SOUTH) {
                return Shape.UPPER_NORTH;
            } else if (facing == Direction.WEST) {
                return Shape.UPPER_EAST;
            } else if (facing == Direction.EAST) {
                return Shape.UPPER_WEST;
            } else {
                return Shape.UPPER_SOUTH;
            }
        } else {
            if (facing == Direction.SOUTH) {
                return Shape.LOWER_NORTH;
            } else if (facing == Direction.WEST) {
                return Shape.LOWER_EAST;
            } else if (facing == Direction.EAST) {
                return Shape.LOWER_WEST;
            } else {
                return Shape.LOWER_SOUTH;
            }
        }
    }

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

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HALF, FACING);
    }

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

    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        var facing = state.get(FACING);
        world.setBlockState(pos.offset(DirectionUtil.getLeft(facing)), state.with(HALF, TripleBlockHalf.TOP), 3);
        world.setBlockState(pos.offset(DirectionUtil.getRight(facing)), state.with(HALF, TripleBlockHalf.LOWER), 3);
    }

    public long getRenderingSeed(BlockState state, BlockPos pos) {
        var half = state.get(HALF);
        var facing = state.get(FACING);
        return switch (half) {
            case TOP -> MathHelper.hashCode(pos.offset(DirectionUtil.getLeft(facing)));
            case UPPER -> super.getRenderingSeed(state, pos);
            case LOWER -> MathHelper.hashCode(pos.offset(DirectionUtil.getRight(facing)));
        };
    }

    private static class Shape {
        protected static VoxelShape TOP_NORTH = Stream.of(
                Block.createCuboidShape(0, 10, 2, 2, 15, 14),
                Block.createCuboidShape(2, 14, 10, 11, 15, 11),
                Block.createCuboidShape(2, 14, 5, 11, 15, 6),
                Block.createCuboidShape(2, 12, 5, 7, 14, 6),
                Block.createCuboidShape(2, 12, 10, 7, 14, 11),
                Block.createCuboidShape(2, 13, 4, 7, 15, 5),
                Block.createCuboidShape(2, 13, 11, 7, 15, 12),
                Block.createCuboidShape(2, 14, 6, 13, 15, 10),
                Block.createCuboidShape(2, 12, 6, 10, 14, 10),
                Block.createCuboidShape(2, 10, 6, 7, 12, 10),
                Block.createCuboidShape(0, 11, 14, 1, 15, 15),
                Block.createCuboidShape(0, 11, 1, 1, 15, 2),
                Block.createCuboidShape(0, 5, 6, 1, 7, 10),
                Block.createCuboidShape(0, 8, 4, 1, 10, 12),
                Block.createCuboidShape(0, 7, 5, 1, 8, 11),
                Block.createCuboidShape(0, 3, 6, 1, 5, 10),
                Block.createCuboidShape(0, 2, 3, 3, 3, 5),
                Block.createCuboidShape(0, 0, 12, 4, 2, 14),
                Block.createCuboidShape(0, 0, 4, 3, 2, 12),
                Block.createCuboidShape(0, 2, 5, 2, 3, 11),
                Block.createCuboidShape(0, 0, 2, 4, 2, 4),
                Block.createCuboidShape(0, 2, 11, 3, 3, 13),
                Block.createCuboidShape(0, 3, 4, 2, 4, 6),
                Block.createCuboidShape(0, 3, 10, 2, 4, 12)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
        protected static VoxelShape TOP_WEST = Stream.of(
                Block.createCuboidShape(2, 10, 14, 14, 15, 16),
                Block.createCuboidShape(10, 14, 5, 11, 15, 14),
                Block.createCuboidShape(5, 14, 5, 6, 15, 14),
                Block.createCuboidShape(5, 12, 9, 6, 14, 14),
                Block.createCuboidShape(10, 12, 9, 11, 14, 14),
                Block.createCuboidShape(4, 13, 9, 5, 15, 14),
                Block.createCuboidShape(11, 13, 9, 12, 15, 14),
                Block.createCuboidShape(6, 14, 3, 10, 15, 14),
                Block.createCuboidShape(6, 12, 6, 10, 14, 14),
                Block.createCuboidShape(6, 10, 9, 10, 12, 14),
                Block.createCuboidShape(14, 11, 15, 15, 15, 16),
                Block.createCuboidShape(1, 11, 15, 2, 15, 16),
                Block.createCuboidShape(6, 5, 15, 10, 7, 16),
                Block.createCuboidShape(4, 8, 15, 12, 10, 16),
                Block.createCuboidShape(5, 7, 15, 11, 8, 16),
                Block.createCuboidShape(6, 3, 15, 10, 5, 16),
                Block.createCuboidShape(3, 2, 13, 5, 3, 16),
                Block.createCuboidShape(12, 0, 12, 14, 2, 16),
                Block.createCuboidShape(4, 0, 13, 12, 2, 16),
                Block.createCuboidShape(5, 2, 14, 11, 3, 16),
                Block.createCuboidShape(2, 0, 12, 4, 2, 16),
                Block.createCuboidShape(11, 2, 13, 13, 3, 16),
                Block.createCuboidShape(4, 3, 14, 6, 4, 16),
                Block.createCuboidShape(10, 3, 14, 12, 4, 16)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
        protected static VoxelShape TOP_EAST = Stream.of(
                Block.createCuboidShape(2, 10, 0, 14, 15, 2),
                Block.createCuboidShape(5, 14, 2, 6, 15, 11),
                Block.createCuboidShape(10, 14, 2, 11, 15, 11),
                Block.createCuboidShape(10, 12, 2, 11, 14, 7),
                Block.createCuboidShape(5, 12, 2, 6, 14, 7),
                Block.createCuboidShape(11, 13, 2, 12, 15, 7),
                Block.createCuboidShape(4, 13, 2, 5, 15, 7),
                Block.createCuboidShape(6, 14, 2, 10, 15, 13),
                Block.createCuboidShape(6, 12, 2, 10, 14, 10),
                Block.createCuboidShape(6, 10, 2, 10, 12, 7),
                Block.createCuboidShape(1, 11, 0, 2, 15, 1),
                Block.createCuboidShape(14, 11, 0, 15, 15, 1),
                Block.createCuboidShape(6, 5, 0, 10, 7, 1),
                Block.createCuboidShape(4, 8, 0, 12, 10, 1),
                Block.createCuboidShape(5, 7, 0, 11, 8, 1),
                Block.createCuboidShape(6, 3, 0, 10, 5, 1),
                Block.createCuboidShape(11, 2, 0, 13, 3, 3),
                Block.createCuboidShape(2, 0, 0, 4, 2, 4),
                Block.createCuboidShape(4, 0, 0, 12, 2, 3),
                Block.createCuboidShape(5, 2, 0, 11, 3, 2),
                Block.createCuboidShape(12, 0, 0, 14, 2, 4),
                Block.createCuboidShape(3, 2, 0, 5, 3, 3),
                Block.createCuboidShape(10, 3, 0, 12, 4, 2),
                Block.createCuboidShape(4, 3, 0, 6, 4, 2)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
        protected static VoxelShape TOP_SOUTH = Stream.of(
                Block.createCuboidShape(14, 10, 2, 16, 15, 14),
                Block.createCuboidShape(5, 14, 5, 14, 15, 6),
                Block.createCuboidShape(5, 14, 10, 14, 15, 11),
                Block.createCuboidShape(9, 12, 10, 14, 14, 11),
                Block.createCuboidShape(9, 12, 5, 14, 14, 6),
                Block.createCuboidShape(9, 13, 11, 14, 15, 12),
                Block.createCuboidShape(9, 13, 4, 14, 15, 5),
                Block.createCuboidShape(3, 14, 6, 14, 15, 10),
                Block.createCuboidShape(6, 12, 6, 14, 14, 10),
                Block.createCuboidShape(9, 10, 6, 14, 12, 10),
                Block.createCuboidShape(15, 11, 1, 16, 15, 2),
                Block.createCuboidShape(15, 11, 14, 16, 15, 15),
                Block.createCuboidShape(15, 5, 6, 16, 7, 10),
                Block.createCuboidShape(15, 8, 4, 16, 10, 12),
                Block.createCuboidShape(15, 7, 5, 16, 8, 11),
                Block.createCuboidShape(15, 3, 6, 16, 5, 10),
                Block.createCuboidShape(13, 2, 11, 16, 3, 13),
                Block.createCuboidShape(12, 0, 2, 16, 2, 4),
                Block.createCuboidShape(13, 0, 4, 16, 2, 12),
                Block.createCuboidShape(14, 2, 5, 16, 3, 11),
                Block.createCuboidShape(12, 0, 12, 16, 2, 14),
                Block.createCuboidShape(13, 2, 3, 16, 3, 5),
                Block.createCuboidShape(14, 3, 10, 16, 4, 12),
                Block.createCuboidShape(14, 3, 4, 16, 4, 6)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

        protected static VoxelShape UPPER_NORTH = Stream.of(
                Block.createCuboidShape(8, 10, 2, 16, 15, 14),
                Block.createCuboidShape(0, 10, 2, 8, 15, 14),
                Block.createCuboidShape(0, 9, 12, 16, 10, 13),
                Block.createCuboidShape(0, 10, 1, 16, 11, 2),
                Block.createCuboidShape(0, 11, 1, 16, 15, 2),
                Block.createCuboidShape(0, 9, 3, 16, 10, 4),
                Block.createCuboidShape(1, 5, 6, 16, 7, 10),
                Block.createCuboidShape(0, 8, 4, 16, 10, 12),
                Block.createCuboidShape(0, 7, 5, 16, 8, 11),
                Block.createCuboidShape(0, 7, 11, 2, 8, 13),
                Block.createCuboidShape(0, 7, 3, 2, 8, 5),
                Block.createCuboidShape(0, 6, 6, 1, 7, 10),
                Block.createCuboidShape(0, 6, 4, 4, 7, 6),
                Block.createCuboidShape(0, 6, 10, 4, 7, 12),
                Block.createCuboidShape(1, 3, 6, 16, 5, 10),
                Block.createCuboidShape(15, 2, 3, 16, 3, 5),
                Block.createCuboidShape(0, 0, 4, 16, 2, 12),
                Block.createCuboidShape(0, 2, 5, 16, 3, 11),
                Block.createCuboidShape(0, 2, 3, 2, 3, 5),
                Block.createCuboidShape(0, 2, 11, 2, 3, 13),
                Block.createCuboidShape(15, 2, 11, 16, 3, 13),
                Block.createCuboidShape(0, 3, 6, 1, 4, 10),
                Block.createCuboidShape(14, 3, 4, 16, 4, 6),
                Block.createCuboidShape(14, 3, 10, 16, 4, 12),
                Block.createCuboidShape(0, 3, 10, 4, 4, 12),
                Block.createCuboidShape(0, 3, 4, 4, 4, 6),
                Block.createCuboidShape(0, 11, 14, 16, 15, 15),
                Block.createCuboidShape(0, 10, 14, 16, 11, 15)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
        protected static VoxelShape UPPER_WEST = Stream.of(
                Block.createCuboidShape(2, 10, 0, 14, 15, 8),
                Block.createCuboidShape(2, 10, 8, 14, 15, 16),
                Block.createCuboidShape(12, 9, 0, 13, 10, 16),
                Block.createCuboidShape(1, 10, 0, 2, 11, 16),
                Block.createCuboidShape(1, 11, 0, 2, 15, 16),
                Block.createCuboidShape(3, 9, 0, 4, 10, 16),
                Block.createCuboidShape(6, 5, 0, 10, 7, 15),
                Block.createCuboidShape(4, 8, 0, 12, 10, 16),
                Block.createCuboidShape(5, 7, 0, 11, 8, 16),
                Block.createCuboidShape(11, 7, 14, 13, 8, 16),
                Block.createCuboidShape(3, 7, 14, 5, 8, 16),
                Block.createCuboidShape(6, 6, 15, 10, 7, 16),
                Block.createCuboidShape(4, 6, 12, 6, 7, 16),
                Block.createCuboidShape(10, 6, 12, 12, 7, 16),
                Block.createCuboidShape(6, 3, 0, 10, 5, 15),
                Block.createCuboidShape(3, 2, 0, 5, 3, 1),
                Block.createCuboidShape(4, 0, 0, 12, 2, 16),
                Block.createCuboidShape(5, 2, 0, 11, 3, 16),
                Block.createCuboidShape(3, 2, 14, 5, 3, 16),
                Block.createCuboidShape(11, 2, 14, 13, 3, 16),
                Block.createCuboidShape(11, 2, 0, 13, 3, 1),
                Block.createCuboidShape(6, 3, 15, 10, 4, 16),
                Block.createCuboidShape(4, 3, 0, 6, 4, 2),
                Block.createCuboidShape(10, 3, 0, 12, 4, 2),
                Block.createCuboidShape(10, 3, 12, 12, 4, 16),
                Block.createCuboidShape(4, 3, 12, 6, 4, 16),
                Block.createCuboidShape(14, 11, 0, 15, 15, 16),
                Block.createCuboidShape(14, 10, 0, 15, 11, 16)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
        protected static VoxelShape UPPER_EAST = Stream.of(
                Block.createCuboidShape(2, 10, 8, 14, 15, 16),
                Block.createCuboidShape(2, 10, 0, 14, 15, 8),
                Block.createCuboidShape(3, 9, 0, 4, 10, 16),
                Block.createCuboidShape(14, 10, 0, 15, 11, 16),
                Block.createCuboidShape(14, 11, 0, 15, 15, 16),
                Block.createCuboidShape(12, 9, 0, 13, 10, 16),
                Block.createCuboidShape(6, 5, 1, 10, 7, 16),
                Block.createCuboidShape(4, 8, 0, 12, 10, 16),
                Block.createCuboidShape(5, 7, 0, 11, 8, 16),
                Block.createCuboidShape(3, 7, 0, 5, 8, 2),
                Block.createCuboidShape(11, 7, 0, 13, 8, 2),
                Block.createCuboidShape(6, 6, 0, 10, 7, 1),
                Block.createCuboidShape(10, 6, 0, 12, 7, 4),
                Block.createCuboidShape(4, 6, 0, 6, 7, 4),
                Block.createCuboidShape(6, 3, 1, 10, 5, 16),
                Block.createCuboidShape(11, 2, 15, 13, 3, 16),
                Block.createCuboidShape(4, 0, 0, 12, 2, 16),
                Block.createCuboidShape(5, 2, 0, 11, 3, 16),
                Block.createCuboidShape(11, 2, 0, 13, 3, 2),
                Block.createCuboidShape(3, 2, 0, 5, 3, 2),
                Block.createCuboidShape(3, 2, 15, 5, 3, 16),
                Block.createCuboidShape(6, 3, 0, 10, 4, 1),
                Block.createCuboidShape(10, 3, 14, 12, 4, 16),
                Block.createCuboidShape(4, 3, 14, 6, 4, 16),
                Block.createCuboidShape(4, 3, 0, 6, 4, 4),
                Block.createCuboidShape(10, 3, 0, 12, 4, 4),
                Block.createCuboidShape(1, 11, 0, 2, 15, 16),
                Block.createCuboidShape(1, 10, 0, 2, 11, 16)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
        protected static VoxelShape UPPER_SOUTH = Stream.of(
                Block.createCuboidShape(0, 10, 2, 8, 15, 14),
                Block.createCuboidShape(8, 10, 2, 16, 15, 14),
                Block.createCuboidShape(0, 9, 3, 16, 10, 4),
                Block.createCuboidShape(0, 10, 14, 16, 11, 15),
                Block.createCuboidShape(0, 11, 14, 16, 15, 15),
                Block.createCuboidShape(0, 9, 12, 16, 10, 13),
                Block.createCuboidShape(0, 5, 6, 15, 7, 10),
                Block.createCuboidShape(0, 8, 4, 16, 10, 12),
                Block.createCuboidShape(0, 7, 5, 16, 8, 11),
                Block.createCuboidShape(14, 7, 3, 16, 8, 5),
                Block.createCuboidShape(14, 7, 11, 16, 8, 13),
                Block.createCuboidShape(15, 6, 6, 16, 7, 10),
                Block.createCuboidShape(12, 6, 10, 16, 7, 12),
                Block.createCuboidShape(12, 6, 4, 16, 7, 6),
                Block.createCuboidShape(0, 3, 6, 15, 5, 10),
                Block.createCuboidShape(0, 2, 11, 1, 3, 13),
                Block.createCuboidShape(0, 0, 4, 16, 2, 12),
                Block.createCuboidShape(0, 2, 5, 16, 3, 11),
                Block.createCuboidShape(14, 2, 11, 16, 3, 13),
                Block.createCuboidShape(14, 2, 3, 16, 3, 5),
                Block.createCuboidShape(0, 2, 3, 1, 3, 5),
                Block.createCuboidShape(15, 3, 6, 16, 4, 10),
                Block.createCuboidShape(0, 3, 10, 2, 4, 12),
                Block.createCuboidShape(0, 3, 4, 2, 4, 6),
                Block.createCuboidShape(12, 3, 4, 16, 4, 6),
                Block.createCuboidShape(12, 3, 10, 16, 4, 12),
                Block.createCuboidShape(0, 11, 1, 16, 15, 2),
                Block.createCuboidShape(0, 10, 1, 16, 11, 2)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

        protected static VoxelShape LOWER_NORTH = Stream.of(
                Block.createCuboidShape(11, 10, 2, 16, 15, 14),
                Block.createCuboidShape(6, 14, 2, 11, 15, 14),
                Block.createCuboidShape(7, 12, 3, 11, 14, 13),
                Block.createCuboidShape(8, 11, 4, 11, 12, 12),
                Block.createCuboidShape(13, 10, 14, 16, 11, 15),
                Block.createCuboidShape(12, 11, 14, 16, 15, 15),
                Block.createCuboidShape(13, 10, 1, 16, 11, 2),
                Block.createCuboidShape(12, 11, 1, 16, 15, 2),
                Block.createCuboidShape(12, 8, 12, 16, 10, 14),
                Block.createCuboidShape(12, 8, 2, 16, 10, 4),
                Block.createCuboidShape(14, 8, 4, 16, 10, 12),
                Block.createCuboidShape(13, 9, 4, 14, 10, 12),
                Block.createCuboidShape(15, 7, 5, 16, 8, 11),
                Block.createCuboidShape(14, 7, 11, 16, 8, 13),
                Block.createCuboidShape(14, 7, 3, 16, 8, 5),
                Block.createCuboidShape(12, 0, 2, 16, 2, 4),
                Block.createCuboidShape(12, 0, 12, 16, 2, 14),
                Block.createCuboidShape(14, 0, 4, 16, 2, 12),
                Block.createCuboidShape(13, 0, 4, 14, 1, 12),
                Block.createCuboidShape(15, 2, 5, 16, 3, 11),
                Block.createCuboidShape(14, 2, 3, 16, 3, 5),
                Block.createCuboidShape(14, 2, 11, 16, 3, 13)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
        protected static VoxelShape LOWER_WEST = Stream.of(
                Block.createCuboidShape(2, 10, 0, 14, 15, 5),
                Block.createCuboidShape(2, 14, 5, 14, 15, 10),
                Block.createCuboidShape(3, 12, 5, 13, 14, 9),
                Block.createCuboidShape(4, 11, 5, 12, 12, 8),
                Block.createCuboidShape(14, 10, 0, 15, 11, 3),
                Block.createCuboidShape(14, 11, 0, 15, 15, 4),
                Block.createCuboidShape(1, 10, 0, 2, 11, 3),
                Block.createCuboidShape(1, 11, 0, 2, 15, 4),
                Block.createCuboidShape(12, 8, 0, 14, 10, 4),
                Block.createCuboidShape(2, 8, 0, 4, 10, 4),
                Block.createCuboidShape(4, 8, 0, 12, 10, 2),
                Block.createCuboidShape(4, 9, 2, 12, 10, 3),
                Block.createCuboidShape(5, 7, 0, 11, 8, 1),
                Block.createCuboidShape(11, 7, 0, 13, 8, 2),
                Block.createCuboidShape(3, 7, 0, 5, 8, 2),
                Block.createCuboidShape(2, 0, 0, 4, 2, 4),
                Block.createCuboidShape(12, 0, 0, 14, 2, 4),
                Block.createCuboidShape(4, 0, 0, 12, 2, 2),
                Block.createCuboidShape(4, 0, 2, 12, 1, 3),
                Block.createCuboidShape(5, 2, 0, 11, 3, 1),
                Block.createCuboidShape(3, 2, 0, 5, 3, 2),
                Block.createCuboidShape(11, 2, 0, 13, 3, 2)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
        protected static VoxelShape LOWER_EAST = Stream.of(
                Block.createCuboidShape(2, 10, 11, 14, 15, 16),
                Block.createCuboidShape(2, 14, 6, 14, 15, 11),
                Block.createCuboidShape(3, 12, 7, 13, 14, 11),
                Block.createCuboidShape(4, 11, 8, 12, 12, 11),
                Block.createCuboidShape(1, 10, 13, 2, 11, 16),
                Block.createCuboidShape(1, 11, 12, 2, 15, 16),
                Block.createCuboidShape(14, 10, 13, 15, 11, 16),
                Block.createCuboidShape(14, 11, 12, 15, 15, 16),
                Block.createCuboidShape(2, 8, 12, 4, 10, 16),
                Block.createCuboidShape(12, 8, 12, 14, 10, 16),
                Block.createCuboidShape(4, 8, 14, 12, 10, 16),
                Block.createCuboidShape(4, 9, 13, 12, 10, 14),
                Block.createCuboidShape(5, 7, 15, 11, 8, 16),
                Block.createCuboidShape(3, 7, 14, 5, 8, 16),
                Block.createCuboidShape(11, 7, 14, 13, 8, 16),
                Block.createCuboidShape(12, 0, 12, 14, 2, 16),
                Block.createCuboidShape(2, 0, 12, 4, 2, 16),
                Block.createCuboidShape(4, 0, 14, 12, 2, 16),
                Block.createCuboidShape(4, 0, 13, 12, 1, 14),
                Block.createCuboidShape(5, 2, 15, 11, 3, 16),
                Block.createCuboidShape(11, 2, 14, 13, 3, 16),
                Block.createCuboidShape(3, 2, 14, 5, 3, 16)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
        protected static VoxelShape LOWER_SOUTH = Stream.of(
                Block.createCuboidShape(0, 10, 2, 5, 15, 14),
                Block.createCuboidShape(5, 14, 2, 10, 15, 14),
                Block.createCuboidShape(5, 12, 3, 9, 14, 13),
                Block.createCuboidShape(5, 11, 4, 8, 12, 12),
                Block.createCuboidShape(0, 10, 1, 3, 11, 2),
                Block.createCuboidShape(0, 11, 1, 4, 15, 2),
                Block.createCuboidShape(0, 10, 14, 3, 11, 15),
                Block.createCuboidShape(0, 11, 14, 4, 15, 15),
                Block.createCuboidShape(0, 8, 2, 4, 10, 4),
                Block.createCuboidShape(0, 8, 12, 4, 10, 14),
                Block.createCuboidShape(0, 8, 4, 2, 10, 12),
                Block.createCuboidShape(2, 9, 4, 3, 10, 12),
                Block.createCuboidShape(0, 7, 5, 1, 8, 11),
                Block.createCuboidShape(0, 7, 3, 2, 8, 5),
                Block.createCuboidShape(0, 7, 11, 2, 8, 13),
                Block.createCuboidShape(0, 0, 12, 4, 2, 14),
                Block.createCuboidShape(0, 0, 2, 4, 2, 4),
                Block.createCuboidShape(0, 0, 4, 2, 2, 12),
                Block.createCuboidShape(2, 0, 4, 3, 1, 12),
                Block.createCuboidShape(0, 2, 5, 1, 3, 11),
                Block.createCuboidShape(0, 2, 11, 2, 3, 13),
                Block.createCuboidShape(0, 2, 3, 2, 3, 5)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
    }
}
