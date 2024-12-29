package dev.magyul.blocks;

import dev.magyul.registers.MTWBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.StairShape;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

import java.util.HashMap;
import java.util.Map;

public class TileStairBlock extends Block {
    private static final Map<Direction, VoxelShape> DEFAULT = new HashMap<>();

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final EnumProperty<StairShape> SHAPE = Properties.STAIR_SHAPE;

    public TileStairBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(SHAPE, StairShape.STRAIGHT));
    }

    @Override
    public boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction direction = state.get(FACING);
        StairShape shape = state.get(SHAPE);
        return switch (shape) {
            case STRAIGHT -> getShapeDefault(state, direction);
            case INNER_LEFT -> getShapeInner(state, direction);
            case INNER_RIGHT -> getShapeInner(state, direction.getOpposite().rotateYCounterclockwise());
            case OUTER_LEFT -> getShapeOuter(state, direction);
            case OUTER_RIGHT -> getShapeOuter(state, direction.getOpposite().rotateYCounterclockwise());
        };
    }

    private VoxelShape getShapeDefault(BlockState state, Direction direction) {
        Map<Direction, VoxelShape> shapes = DEFAULT;
        if (state.isOf(MTWBlocks.SPRUCE_HIGH_SLOPE_TILE)) {
            shapes = Shapes.HighSlopeTile.DEFAULT;
        } else if (state.isOf(MTWBlocks.SPRUCE_LOW_SLOPE_TILE_BOTTOM)) {
            shapes = Shapes.LowSlopeTileBottom.DEFAULT;
        } else if (state.isOf(MTWBlocks.SPRUCE_LOW_SLOPE_TILE_TOP)) {
            shapes = Shapes.LowSlopeTileTop.DEFAULT;
        } else if (state.isOf(MTWBlocks.SPRUCE_LOW_SLOPE_TILE_END)) {
            shapes = Shapes.LowSlopeTileEnd.DEFAULT;
        }
        return shapes.getOrDefault(direction, VoxelShapes.fullCube());
    }

    private VoxelShape getShapeInner(BlockState state, Direction direction) {
        Map<Direction, VoxelShape> shapes = DEFAULT;
        if (state.isOf(MTWBlocks.SPRUCE_HIGH_SLOPE_TILE)) {
            shapes = Shapes.HighSlopeTile.INNER;
        } else if (state.isOf(MTWBlocks.SPRUCE_LOW_SLOPE_TILE_BOTTOM)) {
            shapes = Shapes.LowSlopeTileBottom.INNER;
        } else if (state.isOf(MTWBlocks.SPRUCE_LOW_SLOPE_TILE_TOP)) {
            shapes = Shapes.LowSlopeTileTop.INNER;
        } else if (state.isOf(MTWBlocks.SPRUCE_LOW_SLOPE_TILE_END)) {
            shapes = Shapes.LowSlopeTileEnd.INNER;
        }
        return shapes.getOrDefault(direction, VoxelShapes.fullCube());
    }

    private VoxelShape getShapeOuter(BlockState state, Direction direction) {
        Map<Direction, VoxelShape> shapes = DEFAULT;
        if (state.isOf(MTWBlocks.SPRUCE_HIGH_SLOPE_TILE)) {
            shapes = Shapes.HighSlopeTile.OUTER;
        } else if (state.isOf(MTWBlocks.SPRUCE_LOW_SLOPE_TILE_BOTTOM)) {
            shapes = Shapes.LowSlopeTileBottom.OUTER;
        } else if (state.isOf(MTWBlocks.SPRUCE_LOW_SLOPE_TILE_TOP)) {
            shapes = Shapes.LowSlopeTileTop.OUTER;
        } else if (state.isOf(MTWBlocks.SPRUCE_LOW_SLOPE_TILE_END)) {
            shapes = Shapes.LowSlopeTileEnd.OUTER;
        }
        return shapes.getOrDefault(direction, VoxelShapes.fullCube());
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        BlockState blockState = this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
        return blockState.with(SHAPE, getStairShape(blockState, ctx.getWorld(), blockPos));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess
    world, BlockPos pos, BlockPos neighborPos) {
        return direction.getAxis().isHorizontal() ? state.with(SHAPE, getStairShape(state, world, pos)) : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    private static StairShape getStairShape(BlockState state, BlockView world, BlockPos pos) {
        Direction direction = state.get(FACING);
        BlockState blockState = world.getBlockState(pos.offset(direction));
        if (isStairs(blockState)) {
            Direction direction2 = blockState.get(FACING);
            if (direction2.getAxis() != state.get(FACING).getAxis() && isDifferentOrientation(state, world, pos, direction2.getOpposite())) {
                if (direction2 == direction.rotateYCounterclockwise()) {
                    return StairShape.OUTER_LEFT;
                }

                return StairShape.OUTER_RIGHT;
            }
        }

        BlockState blockState2 = world.getBlockState(pos.offset(direction.getOpposite()));
        if (isStairs(blockState2)) {
            Direction direction3 = blockState2.get(FACING);
            if (direction3.getAxis() != state.get(FACING).getAxis() && isDifferentOrientation(state, world, pos, direction3)) {
                if (direction3 == direction.rotateYCounterclockwise()) {
                    return StairShape.INNER_LEFT;
                }

                return StairShape.INNER_RIGHT;
            }
        }

        return StairShape.STRAIGHT;
    }

    private static boolean isDifferentOrientation(BlockState state, BlockView world, BlockPos pos, Direction dir) {
        BlockState blockState = world.getBlockState(pos.offset(dir));
        return !isStairs(blockState) || blockState.get(FACING) != state.get(FACING);
    }

    public static boolean isStairs(BlockState state) {
        return state.getBlock() instanceof TileStairBlock;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        Direction direction = state.get(FACING);
        StairShape stairShape = state.get(SHAPE);
        switch (mirror) {
            case LEFT_RIGHT:
                if (direction.getAxis() == Direction.Axis.Z) {
                    return switch (stairShape) {
                        case INNER_LEFT ->
                                state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.INNER_RIGHT);
                        case INNER_RIGHT ->
                                state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.INNER_LEFT);
                        case OUTER_LEFT ->
                                state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.OUTER_RIGHT);
                        case OUTER_RIGHT ->
                                state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.OUTER_LEFT);
                        default -> state.rotate(BlockRotation.CLOCKWISE_180);
                    };
                }
                break;
            case FRONT_BACK:
                if (direction.getAxis() == Direction.Axis.X) {
                    return switch (stairShape) {
                        case INNER_LEFT -> state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.INNER_LEFT);
                        case INNER_RIGHT ->
                                state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.INNER_RIGHT);
                        case OUTER_LEFT ->
                                state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.OUTER_RIGHT);
                        case OUTER_RIGHT ->
                                state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.OUTER_LEFT);
                        case STRAIGHT -> state.rotate(BlockRotation.CLOCKWISE_180);
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
}
