package dev.magyul.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;

import java.util.function.BiConsumer;

public class SlabWindowBlock extends HorizontalFacingBlock {
    public static final MapCodec<SlabWindowBlock> CODEC = RecordCodecBuilder.mapCodec((instance) ->
            instance.group(WoodType.CODEC.fieldOf("wood_type").forGetter((block) ->
                    block.type), createSettingsCodec()).apply(instance, SlabWindowBlock::new));
    public static final BooleanProperty OPEN = Properties.OPEN;
    public static final BooleanProperty POWERED = Properties.POWERED;
    protected static final VoxelShape Z_AXIS_SHAPE = Block.createCuboidShape(-4.0, 0.0, 5.0, 20.0, 16.0, 11.0);
    protected static final VoxelShape X_AXIS_SHAPE = Block.createCuboidShape(5.0, 0.0, -4.0, 11.0, 16.0, 20.0);
    protected static final VoxelShape Z_AXIS_COLLISION_SHAPE = Block.createCuboidShape(-4.0, 0.0, 5.0, 20.0, 16.0, 11.0);
    protected static final VoxelShape X_AXIS_COLLISION_SHAPE = Block.createCuboidShape(5.0, 0.0, -4.0, 11.0, 16.0, 20.0);
    protected static final VoxelShape Z_AXIS_SIDES_SHAPE = Block.createCuboidShape(-4.0, 5.0, 5.0, 20.0, 16.0, 11.0);
    protected static final VoxelShape X_AXIS_SIDES_SHAPE = Block.createCuboidShape(5.0, 5.0, -4.0, 11.0, 16.0, 20.0);
    protected static final VoxelShape Z_AXIS_CULL_SHAPE = VoxelShapes.union(Block.createCuboidShape(0.0, 5.0, 6.0, 2.0, 16.0, 10.0), Block.createCuboidShape(14.0, 5.0, 6.0, 16.0, 16.0, 10.0));
    protected static final VoxelShape X_AXIS_CULL_SHAPE = VoxelShapes.union(Block.createCuboidShape(6.0, 5.0, 0.0, 10.0, 16.0, 2.0), Block.createCuboidShape(6.0, 5.0, 14.0, 10.0, 16.0, 16.0));
    private final WoodType type;

    public MapCodec<SlabWindowBlock> getCodec() {
        return CODEC;
    }

    public SlabWindowBlock(WoodType type, AbstractBlock.Settings settings) {
        super(settings.sounds(type.soundType()));
        this.type = type;
        this.setDefaultState(this.stateManager.getDefaultState().with(OPEN, false).with(POWERED, false));
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(FACING).getAxis() == Direction.Axis.X ? X_AXIS_SHAPE : Z_AXIS_SHAPE;
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    protected VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
        if (state.get(OPEN)) {
            return VoxelShapes.empty();
        } else {
            return state.get(FACING).getAxis() == Direction.Axis.Z ? Z_AXIS_SIDES_SHAPE : X_AXIS_SIDES_SHAPE;
        }
    }

    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(OPEN)) {
            return VoxelShapes.empty();
        } else {
            return state.get(FACING).getAxis() == Direction.Axis.Z ? Z_AXIS_COLLISION_SHAPE : X_AXIS_COLLISION_SHAPE;
        }
    }

    protected VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return state.get(FACING).getAxis() == Direction.Axis.X ? X_AXIS_CULL_SHAPE : Z_AXIS_CULL_SHAPE;
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return switch (type) {
            case LAND, AIR -> state.get(OPEN);
            default -> false;
        };
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        boolean bl = world.isReceivingRedstonePower(blockPos);
        Direction direction = ctx.getHorizontalPlayerFacing();
        return this.getDefaultState().with(FACING, direction).with(OPEN, bl).with(POWERED, bl);
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (state.get(OPEN)) {
            state = state.with(OPEN, false);
            world.setBlockState(pos, state, 10);
        } else {
            Direction direction = player.getHorizontalFacing();
            if (state.get(FACING) == direction.getOpposite()) {
                state = state.with(FACING, direction);
            }

            state = state.with(OPEN, true);
            world.setBlockState(pos, state, 10);
        }

        boolean bl = state.get(OPEN);
        world.playSound(player, pos, bl ? this.type.fenceGateOpen() : this.type.fenceGateClose(), SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.1F + 0.9F);
        world.emitGameEvent(player, bl ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
        return ActionResult.success(world.isClient);
    }

    protected void onExploded(BlockState state, World world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
        if (explosion.getDestructionType() == Explosion.DestructionType.TRIGGER_BLOCK && !world.isClient() && !(Boolean)state.get(POWERED)) {
            boolean bl = state.get(OPEN);
            world.setBlockState(pos, state.with(OPEN, !bl));
            world.playSound(null, pos, bl ? this.type.fenceGateClose() : this.type.fenceGateOpen(), SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.1F + 0.9F);
            world.emitGameEvent(bl ? GameEvent.BLOCK_CLOSE : GameEvent.BLOCK_OPEN, pos, GameEvent.Emitter.of(state));
        }

        super.onExploded(state, world, pos, explosion, stackMerger);
    }

    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient) {
            boolean bl = world.isReceivingRedstonePower(pos);
            if (state.get(POWERED) != bl) {
                world.setBlockState(pos, state.with(POWERED, bl).with(OPEN, bl), 2);
                if (state.get(OPEN) != bl) {
                    world.playSound(null, pos, bl ? this.type.fenceGateOpen() : this.type.fenceGateClose(), SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.1F + 0.9F);
                    world.emitGameEvent(null, bl ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
                }
            }

        }
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN, POWERED);
    }
}
