package dev.magyul.blocks;

import dev.magyul.blocks.enums.TripleBlockHalf;
import dev.magyul.registers.MTWProperties;
import dev.magyul.util.MathHelper;
import net.minecraft.block.*;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class BigDoorBlock extends Block {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty OPEN = Properties.OPEN;
    public static final EnumProperty<DoorHinge> HINGE = Properties.DOOR_HINGE;
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final EnumProperty<TripleBlockHalf> HALF = MTWProperties.TRIPLE_BLOCK_HALF;
    protected static final VoxelShape Z_AXIS_SHAPE = Block.createCuboidShape(0.0, 0.0, 5.0, 16.0, 16.0, 11.0);
    protected static final VoxelShape X_AXIS_SHAPE = Block.createCuboidShape(5.0, 0.0, 0.0, 11.0, 16.0, 16.0);
    protected static final VoxelShape Z_AXIS_COLLISION_SHAPE = Block.createCuboidShape(0.0, 0.0, 5.0, 20.0, 16.0, 11.0);
    protected static final VoxelShape X_AXIS_COLLISION_SHAPE = Block.createCuboidShape(5.0, 0.0, 0.0, 11.0, 16.0, 16.0);
    protected static final VoxelShape Z_AXIS_SIDES_SHAPE = Block.createCuboidShape(0.0, 5.0, 5.0, 16.0, 16.0, 11.0);
    protected static final VoxelShape X_AXIS_SIDES_SHAPE = Block.createCuboidShape(5.0, 5.0, 0.0, 11.0, 16.0, 16.0);
    protected static final VoxelShape Z_AXIS_CULL_SHAPE = VoxelShapes.union(Block.createCuboidShape(0.0, 5.0, 6.0, 2.0, 16.0, 10.0), Block.createCuboidShape(14.0, 5.0, 6.0, 16.0, 16.0, 10.0));
    protected static final VoxelShape X_AXIS_CULL_SHAPE = VoxelShapes.union(Block.createCuboidShape(6.0, 5.0, 0.0, 10.0, 16.0, 2.0), Block.createCuboidShape(6.0, 5.0, 14.0, 10.0, 16.0, 16.0));
    private final BlockSetType blockSetType;

    public BigDoorBlock(BlockSetType type, AbstractBlock.Settings settings) {
        super(settings.sounds(type.soundType()));
        this.blockSetType = type;
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(OPEN, false).with(HINGE, DoorHinge.LEFT).with(POWERED, false).with(HALF, TripleBlockHalf.LOWER));
    }

    public BlockSetType getBlockSetType() {
        return this.blockSetType;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(FACING).getAxis() == Direction.Axis.X ? X_AXIS_SHAPE : Z_AXIS_SHAPE;
    }

    @Override
    public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
        if (state.get(OPEN)) {
            return VoxelShapes.empty();
        } else {
            return state.get(FACING).getAxis() == Direction.Axis.Z ? Z_AXIS_SIDES_SHAPE : X_AXIS_SIDES_SHAPE;
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(OPEN)) {
            return VoxelShapes.empty();
        } else {
            return state.get(FACING).getAxis() == Direction.Axis.Z ? Z_AXIS_COLLISION_SHAPE : X_AXIS_COLLISION_SHAPE;
        }
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return state.get(FACING).getAxis() == Direction.Axis.X ? X_AXIS_CULL_SHAPE : Z_AXIS_CULL_SHAPE;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
//        MTWMod.LOGGER.info("getStateForNeighborUpdate: state({}) / neighborState({})", state.get(HALF), neighborState.get(HALF));
        TripleBlockHalf doubleBlockHalf = state.get(HALF);
        if (direction.getAxis() == Direction.Axis.Y) {
            if (doubleBlockHalf == TripleBlockHalf.LOWER == (direction == Direction.UP)) {
                return neighborState.getBlock() instanceof BigDoorBlock && neighborState.get(HALF) != doubleBlockHalf ? neighborState.with(HALF, doubleBlockHalf) : Blocks.AIR.getDefaultState();
            }
            if (doubleBlockHalf == TripleBlockHalf.UPPER == (direction == Direction.UP)) {
                return neighborState.getBlock() instanceof BigDoorBlock && neighborState.get(HALF) != doubleBlockHalf ? neighborState.with(HALF, doubleBlockHalf) : Blocks.AIR.getDefaultState();
            }
        } else {
            return doubleBlockHalf == TripleBlockHalf.LOWER && direction == Direction.DOWN && !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient && (player.isCreative() || !player.canHarvest(state))) {
            TripleBlockHalf doubleBlockHalf = state.get(HALF);
            if (doubleBlockHalf == TripleBlockHalf.TOP) {
                var blockPos = pos.down();
                BlockState blockState = world.getBlockState(blockPos);
                if (blockState.isOf(state.getBlock()) && blockState.get(HALF) == TripleBlockHalf.UPPER) {
                    BlockState blockState2 = blockState.getFluidState().isOf(Fluids.WATER) ? Blocks.WATER.getDefaultState() : Blocks.AIR.getDefaultState();
                    world.setBlockState(blockPos, blockState2, 35);
                    world.syncWorldEvent(player, 2001, blockPos, Block.getRawIdFromState(blockState));
                }
            }
            if (doubleBlockHalf == TripleBlockHalf.UPPER) {
                BlockPos blockPos = pos.down();
                BlockState blockState = world.getBlockState(blockPos);
                if (blockState.isOf(state.getBlock()) && blockState.get(HALF) == TripleBlockHalf.LOWER) {
                    BlockState blockState2 = blockState.getFluidState().isOf(Fluids.WATER) ? Blocks.WATER.getDefaultState() : Blocks.AIR.getDefaultState();
                    world.setBlockState(blockPos, blockState2, 35);
                    world.syncWorldEvent(player, 2001, blockPos, Block.getRawIdFromState(blockState));
                }
            }
        }

        super.onBreak(world, pos, state, player);
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        switch (type) {
            case LAND, AIR -> {
                return state.get(OPEN);
            }
            default -> {
                return false;
            }
        }
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        World world = ctx.getWorld();
        var upper = world.getBlockState(blockPos.up()).canReplace(ctx);
        var top = world.getBlockState(blockPos.up().up()).canReplace(ctx);
        if (blockPos.getY() < world.getTopY() - 2 && upper && top) {
            boolean bl = world.isReceivingRedstonePower(blockPos) || world.isReceivingRedstonePower(blockPos.up());
            return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing()).with(HINGE, this.getHinge(ctx)).with(POWERED, bl).with(OPEN, bl).with(HALF, TripleBlockHalf.LOWER);
        } else {
            return null;
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        if (state.get(HALF) == TripleBlockHalf.TOP) return BlockRenderType.INVISIBLE;
        else return super.getRenderType(state);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        world.setBlockState(pos.up(), state.with(HALF, TripleBlockHalf.UPPER), 3);
        world.setBlockState(pos.up().up(), state.with(HALF, TripleBlockHalf.TOP), 3);
    }

    private DoorHinge getHinge(ItemPlacementContext ctx) {
        BlockView blockView = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        Direction direction = ctx.getHorizontalPlayerFacing();
        BlockPos blockPos2 = blockPos.up();
        Direction direction2 = direction.rotateYCounterclockwise();
        BlockPos blockPos3 = blockPos.offset(direction2);
        BlockState blockState = blockView.getBlockState(blockPos3);
        BlockPos blockPos4 = blockPos2.offset(direction2);
        BlockState blockState2 = blockView.getBlockState(blockPos4);
        Direction direction3 = direction.rotateYClockwise();
        BlockPos blockPos5 = blockPos.offset(direction3);
        BlockState blockState3 = blockView.getBlockState(blockPos5);
        BlockPos blockPos6 = blockPos2.offset(direction3);
        BlockState blockState4 = blockView.getBlockState(blockPos6);
        int i = (blockState.isFullCube(blockView, blockPos3) ? -1 : 0) + (blockState2.isFullCube(blockView, blockPos4) ? -1 : 0) + (blockState3.isFullCube(blockView, blockPos5) ? 1 : 0) + (blockState4.isFullCube(blockView, blockPos6) ? 1 : 0);
        boolean bl = blockState.isOf(this) && blockState.get(HALF) == TripleBlockHalf.LOWER;
        boolean bl2 = blockState3.isOf(this) && blockState3.get(HALF) == TripleBlockHalf.LOWER;
        if ((!bl || bl2) && i <= 0) {
            if ((!bl2 || bl) && i == 0) {
                int j = direction.getOffsetX();
                int k = direction.getOffsetZ();
                Vec3d vec3d = ctx.getHitPos();
                double d = vec3d.x - (double)blockPos.getX();
                double e = vec3d.z - (double)blockPos.getZ();
                return (j >= 0 || !(e < 0.5)) && (j <= 0 || !(e > 0.5)) && (k >= 0 || !(d > 0.5)) && (k <= 0 || !(d < 0.5)) ? DoorHinge.LEFT : DoorHinge.RIGHT;
            } else {
                return DoorHinge.LEFT;
            }
        } else {
            return DoorHinge.RIGHT;
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
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

    public boolean isOpen(BlockState state) {
        return state.get(OPEN);
    }

    public void setOpen(@Nullable Entity entity, World world, BlockState state, BlockPos pos, boolean open) {
        if (state.isOf(this) && state.get(OPEN) != open) {
            world.setBlockState(pos, state.with(OPEN, open), 10);
            this.playOpenCloseSound(entity, world, pos, open);
            world.emitGameEvent(entity, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        boolean bl = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.offset(state.get(HALF) == TripleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));
        if (!this.getDefaultState().isOf(sourceBlock) && bl != state.get(POWERED)) {
            if (bl != state.get(OPEN)) {
                this.playOpenCloseSound(null, world, pos, bl);
                world.emitGameEvent(null, bl ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
            }

            world.setBlockState(pos, state.with(POWERED, bl).with(OPEN, bl), 2);
        }

    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);
        return state.get(HALF) == TripleBlockHalf.LOWER ? blockState.isSideSolidFullSquare(world, blockPos, Direction.UP) : blockState.isOf(this);
    }

    private void playOpenCloseSound(@Nullable Entity entity, World world, BlockPos pos, boolean open) {
        world.playSound(entity, pos, open ? this.blockSetType.doorOpen() : this.blockSetType.doorClose(), SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return mirror == BlockMirror.NONE ? state : state.rotate(mirror.getRotation(state.get(FACING))).cycle(HINGE);
    }

    @Override
    public long getRenderingSeed(BlockState state, BlockPos pos) {
        return MathHelper.hashCode(pos.getX(), pos.down(state.get(HALF) == TripleBlockHalf.LOWER ? 0 : 1).getY(), pos.getZ());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HALF, FACING, OPEN, HINGE, POWERED);
    }
}
