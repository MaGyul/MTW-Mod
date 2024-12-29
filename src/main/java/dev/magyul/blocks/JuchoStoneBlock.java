package dev.magyul.blocks;

import dev.magyul.blocks.enums.JuchoShape;
import dev.magyul.registers.MTWBlocks;
import dev.magyul.registers.MTWProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class JuchoStoneBlock extends Block {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final EnumProperty<JuchoShape> SHAPE = MTWProperties.JUCHO_SHAPE;
    private static final VoxelShape TOP = Block.createCuboidShape(0, 13, 0, 16, 16, 16);
    private static final VoxelShape MIDDLE = Block.createCuboidShape(4, 8, 4, 12, 13, 12);
    private static final VoxelShape BOTTOM = Block.createCuboidShape(0, 0, 0, 16, 8, 16);
    private static final VoxelShape BASE = VoxelShapes.union(BOTTOM, TOP, MIDDLE);

    public JuchoStoneBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(SHAPE, JuchoShape.STRAIGHT));
    }

    @Override
    public boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return BASE;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos pos = ctx.getBlockPos();
        var world = ctx.getWorld();
        var facing = ctx.getHorizontalPlayerFacing();
        BlockState state = this.getDefaultState().with(FACING, facing);
        return state.with(SHAPE, getStairShape(state, world, pos));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return direction.getAxis().isHorizontal() ?
                state.with(SHAPE, getStairShape(state, world, pos)) :
                super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    private static JuchoShape getStairShape(BlockState state, BlockView world, BlockPos pos) {
        Direction direction = state.get(FACING);
        BlockState blockState = world.getBlockState(pos.offset(direction));
        if (isStairs(blockState)) {
            Direction direction2 = blockState.get(FACING);
            if (direction2.getAxis() != state.get(FACING).getAxis() && isDifferentOrientation(state, world, pos, direction2.getOpposite())) {
                if (direction2 == direction.rotateYCounterclockwise()) {
                    return JuchoShape.OUTER_LEFT;
                }

                return JuchoShape.OUTER_RIGHT;
            }
        }

        BlockState blockState2 = world.getBlockState(pos.offset(direction.getOpposite()));
        if (isStairs(blockState2)) {
            Direction direction3 = blockState2.get(FACING);
            if (direction3.getAxis() != state.get(FACING).getAxis() && isDifferentOrientation(state, world, pos, direction3)) {
                if (direction3 == direction.rotateYCounterclockwise()) {
                    return JuchoShape.INNER_LEFT;
                }

                return JuchoShape.INNER_RIGHT;
            }
        }

        if (hasLeft(pos, world, direction) && hasRight(pos, world, direction)) {
            return JuchoShape.MIDDLE;
        }

        if (isCenter(pos, world)) {
            return JuchoShape.CLOSED;
        }

        return JuchoShape.STRAIGHT;
    }

    private static boolean isDifferentOrientation(BlockState state, BlockView world, BlockPos pos, Direction dir) {
        BlockState blockState = world.getBlockState(pos.offset(dir));
        return !isStairs(blockState) || blockState.get(FACING) != state.get(FACING);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        Direction direction = state.get(FACING);
        JuchoShape stairShape = state.get(SHAPE);
        switch (mirror) {
            case LEFT_RIGHT:
                if (direction.getAxis() == Direction.Axis.Z) {
                    return switch (stairShape) {
                        case INNER_LEFT ->
                                state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, JuchoShape.INNER_RIGHT);
                        case INNER_RIGHT ->
                                state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, JuchoShape.INNER_LEFT);
                        case OUTER_LEFT ->
                                state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, JuchoShape.OUTER_RIGHT);
                        case OUTER_RIGHT ->
                                state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, JuchoShape.OUTER_LEFT);
                        default -> state.rotate(BlockRotation.CLOCKWISE_180);
                    };
                }
                break;
            case FRONT_BACK:
                if (direction.getAxis() == Direction.Axis.X) {
                    return switch (stairShape) {
                        case INNER_LEFT -> state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, JuchoShape.INNER_LEFT);
                        case INNER_RIGHT ->
                                state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, JuchoShape.INNER_RIGHT);
                        case OUTER_LEFT ->
                                state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, JuchoShape.OUTER_RIGHT);
                        case OUTER_RIGHT ->
                                state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, JuchoShape.OUTER_LEFT);
                        case STRAIGHT, MIDDLE, CLOSED -> state.rotate(BlockRotation.CLOCKWISE_180);
                    };
                }
        }

        return super.mirror(state, mirror);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, SHAPE);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    private static boolean hasLeft(BlockPos pos, BlockView world, Direction facing) {
        BlockPos leftPos = null;
        if (facing == Direction.NORTH)  {
            leftPos = pos.west();
        } else if (facing == Direction.WEST) {
            leftPos = pos.south();
        } else if (facing == Direction.SOUTH) {
            leftPos = pos.east();
        } else if (facing == Direction.EAST) {
            leftPos = pos.north();
        }

        if (leftPos == null) return false;
        var blockState = world.getBlockState(leftPos);
        return blockState.isOf(MTWBlocks.SPRUCE_LARGE_HALL_LINOLEUM);
    }

    private static boolean hasRight(BlockPos pos, BlockView world, Direction facing) {
        BlockPos leftPos = null;
        if (facing == Direction.NORTH)  {
            leftPos = pos.east();
        } else if (facing == Direction.WEST) {
            leftPos = pos.north();
        } else if (facing == Direction.SOUTH) {
            leftPos = pos.west();
        } else if (facing == Direction.EAST) {
            leftPos = pos.south();
        }

        if (leftPos == null) return false;
        var blockState = world.getBlockState(leftPos);
        return blockState.isOf(MTWBlocks.SPRUCE_LARGE_HALL_LINOLEUM);
    }

    private static boolean isCenter(BlockPos pos, BlockView world) {
        boolean result = isLinoleum(pos.east(), world);
        if (!isLinoleum(pos.north(), world)) result = false;
        if (!isLinoleum(pos.west(), world)) result = false;
        if (!isLinoleum(pos.south(), world)) result = false;
        return result;
    }

    private static boolean isStairs(BlockState state) {
        return state.getBlock() instanceof MoreSlabStairBlock || state.getBlock() instanceof JuchoStoneBlock;
    }

    private static boolean isLinoleum(BlockPos pos, BlockView world) {
        return world.getBlockState(pos).isOf(MTWBlocks.SPRUCE_LINOLEUM);
    }
}
