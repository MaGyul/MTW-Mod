package dev.magyul.blocks;

import dev.magyul.registers.MTWSounds;
import net.minecraft.block.*;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class JapaneseDoors extends DoorBlock {
    protected static final VoxelShape EAST = Block.createCuboidShape(
            1.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);
    protected static final VoxelShape WEST = Block.createCuboidShape(
            13.0d, 0.0d, 0.0d, 15.0d, 16.0d, 16.0d);
    protected static final VoxelShape SOUTH = Block.createCuboidShape(
            0.0d, 0.0d, 1.0d, 16.0d, 16.0d, 3.0d);
    protected static final VoxelShape NORTH = Block.createCuboidShape(
            0.0d, 0.0d, 13.0d, 16.0d, 16.0d, 15.0d);


    protected static final VoxelShape EAST_LEFT_OPEN = Block.createCuboidShape(
            1.0D, 0.0D, -12.0d, 3.0D, 16.0D, 4.0d);
    protected static final VoxelShape EAST_RIGHT_OPEN = Block.createCuboidShape(
            1.0D, 0.0D, 12.0d, 3.0D, 16.0D, 28.0d);


    protected static final VoxelShape WEST_LEFT_OPEN = Block.createCuboidShape(
            13.0d, 0.0d, 12.0d, 15.0d, 16.0d, 28.0d);
    protected static final VoxelShape WEST_RIGHT_OPEN = Block.createCuboidShape(
            13.0d, 0.0d, -12.0d, 15.0d, 16.0d, 4.0d);


    protected static final VoxelShape SOUTH_LEFT_OPEN = Block.createCuboidShape(
            12.0d, 0.0d, 1.0d, 28.0d, 16.0d, 3.0d);
    protected static final VoxelShape SOUTH_RIGHT_OPEN = Block.createCuboidShape(
            -12.0d, 0.0d, 1.0d, 4.0d, 16.0d, 3.0d);


    protected static final VoxelShape NORTH_LEFT_OPEN = Block.createCuboidShape(
            -12.0d, 0.0d, 13.0d, 4.0d, 16.0d, 15.0d);
    protected static final VoxelShape NORTH_RIGHT_OPEN = Block.createCuboidShape(
            12.0d, 0.0d, 13.0d, 28.0d, 16.0d, 15.0d);
    protected final BlockSetType blockSetType;

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(OPEN)) {
            if (state.get(HINGE) == DoorHinge.LEFT) {
                return switch (state.get(FACING)) {
                    case EAST -> EAST_LEFT_OPEN;
                    case WEST -> WEST_LEFT_OPEN;
                    case NORTH -> NORTH_LEFT_OPEN;
                    case SOUTH -> SOUTH_LEFT_OPEN;
                    default -> VoxelShapes.empty();
                };
            } else {
                return switch (state.get(FACING)) {
                    case EAST -> EAST_RIGHT_OPEN;
                    case WEST -> WEST_RIGHT_OPEN;
                    case NORTH -> NORTH_RIGHT_OPEN;
                    case SOUTH -> SOUTH_RIGHT_OPEN;
                    default -> VoxelShapes.empty();
                };
            }
        } else {
            return switch (state.get(FACING)) {
                case EAST -> EAST;
                case WEST -> WEST;
                case NORTH -> NORTH;
                case SOUTH -> SOUTH;
                default -> VoxelShapes.empty();
            };
        }
    }

    public JapaneseDoors(BlockSetType type, Settings settings) {
        super(type, settings);
        setDefaultState(stateManager.getDefaultState().with(FACING, Direction.NORTH)
                .with(OPEN, false).with(HINGE, DoorHinge.LEFT)
                .with(POWERED, false).with(HALF, DoubleBlockHalf.LOWER));
        this.blockSetType = type;
    }

    @Override
    public void onStateReplaced(BlockState oldState, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (newState.isOf(this)) {
            boolean left = newState.get(HINGE) == DoorHinge.LEFT;
            var facing = newState.get(FACING);
            BlockPos facingPos = null;
            switch (facing) {
                case SOUTH:
                    if (left) {
                        facingPos = pos.east();
                    } else {
                        facingPos = pos.west();
                    }
                    break;
                case NORTH:
                    if (left) {
                        facingPos = pos.west();
                    } else {
                        facingPos = pos.east();
                    }
                    break;
                case WEST:
                    if (left) {
                        facingPos = pos.south();
                    } else {
                        facingPos = pos.north();
                    }
                    break;
                case EAST:
                    if (left) {
                        facingPos = pos.north();
                    } else {
                        facingPos = pos.south();
                    }
                    break;
            }
            if (facingPos != null) {
                var state = world.getBlockState(facingPos);
                if (state.getBlock() instanceof JapaneseDoors) {
                    state = state.with(OPEN, newState.get(OPEN));
                    world.setBlockState(facingPos, state, 10);
                }
                return;
            }
        }
        super.onStateReplaced(oldState, world, pos, newState, moved);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HALF, FACING, OPEN, HINGE, POWERED);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!this.blockSetType.canOpenByHand()) {
            return ActionResult.PASS;
        } else {
            state = state.cycle(OPEN);
            world.setBlockState(pos, state, 10);
            this.playOpenCloseSound(player, world, pos, state.get(OPEN));
            world.emitGameEvent(player, this.isOpen(state) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
            return ActionResult.success(world.isClient);
        }
    }

    @Override
    public void setOpen(@Nullable Entity entity, World world, BlockState state, BlockPos pos, boolean open) {
        if (state.isOf(this) && state.get(OPEN) != open) {
            world.setBlockState(pos, state.with(OPEN, open), 10);
            this.playOpenCloseSound(entity, world, pos, open);
            world.emitGameEvent(entity, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        boolean bl = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.offset(state.get(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));
        if (!this.getDefaultState().isOf(sourceBlock) && bl != state.get(POWERED)) {
            if (bl != state.get(OPEN)) {
                this.playOpenCloseSound(null, world, pos, bl);
                world.emitGameEvent(null, bl ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
            }

            world.setBlockState(pos, state.with(POWERED, bl).with(OPEN, bl), 2);
        }
    }

    private void playOpenCloseSound(@Nullable Entity entity, World world, BlockPos pos, boolean ignoredOpen) {
        world.playSound(entity, pos, MTWSounds.SHOJI, SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.1F + 0.9F);
    }
}
