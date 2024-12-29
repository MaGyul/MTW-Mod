package dev.magyul.blocks;

import dev.magyul.blocks.enums.BlockLR;
import dev.magyul.registers.MTWProperties;
import dev.magyul.util.DirectionUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.WoodType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class SignBoard extends Block {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<BlockLR> BLOCK_LR = MTWProperties.BLOCK_LR;

    protected static final VoxelShape SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
    private final WoodType type;

    public SignBoard(WoodType type, Settings settings) {
        super(settings);
        this.type = type;
        setDefaultState(stateManager.getDefaultState().with(FACING, Direction.NORTH).with(BLOCK_LR, BlockLR.Single));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, BLOCK_LR);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (neighborState.isAir() || neighborState.getBlock() instanceof SignBoard) {
            var facing = state.get(FACING);
            var blr = BlockLR.Single;
            if (hasLeft(pos, world, facing) && hasRight(pos, world, facing)) {
                blr = BlockLR.Middle;
            } else if (hasLeft(pos, world, facing)) {
                blr = BlockLR.Right;
            } else if (hasRight(pos, world, facing)) {
                blr = BlockLR.Left;
            }

            return state.with(BLOCK_LR, blr);
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        var pos = ctx.getBlockPos();
        var world = ctx.getWorld();
        var facing = ctx.getHorizontalPlayerFacing();
        var blr = BlockLR.Single;
        if (hasLeft(pos, world, facing) && hasRight(pos, world, facing)) {
            blr = BlockLR.Middle;
        } else if (hasLeft(pos, world, facing)) {
            blr = BlockLR.Right;
        } else if (hasRight(pos, world, facing))  {
            blr = BlockLR.Left;
        }
        return this.getDefaultState().with(FACING, facing).with(BLOCK_LR, blr);
    }

    public WoodType getWoodType() {
        return type;
    }

    private boolean hasLeft(BlockPos pos, WorldAccess world, Direction facing) {
        var left = DirectionUtil.getLeftNull(facing);
        BlockPos leftPos = left == null ? null : pos.offset(left);
        if (leftPos == null) return false;
        var blockState = world.getBlockState(leftPos);
        return blockState.getBlock() instanceof SignBoard;
    }

    private boolean hasRight(BlockPos pos, WorldAccess world, Direction facing) {
        var right = DirectionUtil.getRightNull(facing);
        BlockPos rightPos = right == null ? null : pos.offset(right);
        if (rightPos == null) return false;
        var blockState = world.getBlockState(rightPos);
        return blockState.getBlock() instanceof SignBoard;
    }
}
