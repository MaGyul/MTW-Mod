package dev.magyul.blocks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import dev.magyul.blocks.enums.SideWall;
import dev.magyul.registers.MTWProperties;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.LeadItem;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.Map;
import java.util.Objects;

public class WallBlock extends Block implements Waterloggable {
    public static final EnumProperty<SideWall> NORTH = MTWProperties.SIDE_WALL_NORTH;
    public static final EnumProperty<SideWall> EAST = MTWProperties.SIDE_WALL_EAST;
    public static final EnumProperty<SideWall> SOUTH = MTWProperties.SIDE_WALL_SOUTH;
    public static final EnumProperty<SideWall> WEST = MTWProperties.SIDE_WALL_WEST;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    protected static final Map<Direction, EnumProperty<SideWall>> FACING_PROPERTIES = ImmutableMap.copyOf(Util.make(Maps.newEnumMap(Direction.class), (directions) -> {
        directions.put(Direction.NORTH, NORTH);
        directions.put(Direction.EAST, EAST);
        directions.put(Direction.SOUTH, SOUTH);
        directions.put(Direction.WEST, WEST);
    }));
    protected final VoxelShape[] collisionShapes;
    protected final VoxelShape[] boundingShapes;
    private final VoxelShape[] cullingShapes;
    private final Object2IntMap<BlockState> SHAPE_INDEX_CACHE = new Object2IntOpenHashMap<>();

    public WallBlock(Settings settings) {
        super(settings);
        float radius1 = 3.0F;
        float radius2 = 3.0F;
        float boundingHeight1 = 16.0F;
        float boundingHeight2 = 16.0F;
        float collisionHeight = 16.0F;

        for (BlockState blockState : this.stateManager.getStates()) {
            this.getShapeIndex(blockState);
        }
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(NORTH, SideWall.NOTTING)
                .with(EAST, SideWall.NOTTING)
                .with(SOUTH, SideWall.NOTTING)
                .with(WEST, SideWall.NOTTING)
                .with(WATERLOGGED, false));
        this.collisionShapes = this.createShapes(radius1, radius2, collisionHeight, 0.0F, collisionHeight);
        this.boundingShapes = this.createShapes(radius1, radius2, boundingHeight1, 0.0F, boundingHeight2);
        this.cullingShapes = this.createShapes(3.0F, 2.0F, 16.0F, 6.0F, 15.0F);
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return this.cullingShapes[this.getShapeIndex(state)];
    }

    @Override
    public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.getOutlineShape(state, world, pos, context);
    }

    public SideWall canConnect(BlockState state) {
        if (state.isAir()) return SideWall.NOTTING;
        return SideWall.parse(state);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        return !world.isClient() ? LeadItem.attachHeldMobsToBlock(player, world, pos) : ActionResult.PASS;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockView blockView = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        BlockPos blockPos2 = blockPos.north();
        BlockPos blockPos3 = blockPos.east();
        BlockPos blockPos4 = blockPos.south();
        BlockPos blockPos5 = blockPos.west();
        BlockState blockState = blockView.getBlockState(blockPos2);
        BlockState blockState2 = blockView.getBlockState(blockPos3);
        BlockState blockState3 = blockView.getBlockState(blockPos4);
        BlockState blockState4 = blockView.getBlockState(blockPos5);
        return Objects.requireNonNull(super.getPlacementState(ctx))
                .with(NORTH, this.canConnect(blockState))
                .with(EAST, this.canConnect(blockState2))
                .with(SOUTH, this.canConnect(blockState3))
                .with(WEST, this.canConnect(blockState4))
                .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        if (direction.getAxis().getType() == Direction.Type.HORIZONTAL) {
            return state.with(FACING_PROPERTIES.get(direction), this.canConnect(neighborState));
        } else {
            return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
    }

    protected boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return !state.get(WATERLOGGED);
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.boundingShapes[this.getShapeIndex(state)];
    }

    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.collisionShapes[this.getShapeIndex(state)];
    }

    protected FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return switch (rotation) {
            case CLOCKWISE_180 ->
                    state.with(NORTH, state.get(SOUTH)).with(EAST, state.get(WEST)).with(SOUTH, state.get(NORTH)).with(WEST, state.get(EAST));
            case COUNTERCLOCKWISE_90 ->
                    state.with(NORTH, state.get(EAST)).with(EAST, state.get(SOUTH)).with(SOUTH, state.get(WEST)).with(WEST, state.get(NORTH));
            case CLOCKWISE_90 ->
                    state.with(NORTH, state.get(WEST)).with(EAST, state.get(NORTH)).with(SOUTH, state.get(EAST)).with(WEST, state.get(SOUTH));
            default -> state;
        };
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return switch (mirror) {
            case LEFT_RIGHT ->
                    state.with(NORTH, state.get(SOUTH)).with(SOUTH, state.get(NORTH));
            case FRONT_BACK ->
                    state.with(EAST, state.get(WEST)).with(WEST, state.get(EAST));
            default -> super.mirror(state, mirror);
        };
    }

    protected VoxelShape[] createShapes(float radius1, float radius2, float height1, float offset2, float height2) {
        float f = 8.0F - radius1;
        float g = 8.0F + radius1;
        float h = 8.0F - radius2;
        float i = 8.0F + radius2;
        VoxelShape voxelShape = Block.createCuboidShape(f, 0.0, f, g, height1, g);
        VoxelShape voxelShape2 = Block.createCuboidShape(h, offset2, 0.0, i, height2, i);
        VoxelShape voxelShape3 = Block.createCuboidShape(h, offset2, h, i, height2, 16.0);
        VoxelShape voxelShape4 = Block.createCuboidShape(0.0, offset2, h, i, height2, i);
        VoxelShape voxelShape5 = Block.createCuboidShape(h, offset2, h, 16.0, height2, i);
        VoxelShape voxelShape6 = VoxelShapes.union(voxelShape2, voxelShape5);
        VoxelShape voxelShape7 = VoxelShapes.union(voxelShape3, voxelShape4);
        VoxelShape[] voxelShapes = new VoxelShape[]{VoxelShapes.empty(), voxelShape3, voxelShape4, voxelShape7, voxelShape2, VoxelShapes.union(voxelShape3, voxelShape2), VoxelShapes.union(voxelShape4, voxelShape2), VoxelShapes.union(voxelShape7, voxelShape2), voxelShape5, VoxelShapes.union(voxelShape3, voxelShape5), VoxelShapes.union(voxelShape4, voxelShape5), VoxelShapes.union(voxelShape7, voxelShape5), voxelShape6, VoxelShapes.union(voxelShape3, voxelShape6), VoxelShapes.union(voxelShape4, voxelShape6), VoxelShapes.union(voxelShape7, voxelShape6)};

        for(int j = 0; j < 16; ++j) {
            voxelShapes[j] = VoxelShapes.union(voxelShape, voxelShapes[j]);
        }

        return voxelShapes;
    }

    private static int getDirectionMask(Direction dir) {
        return 1 << dir.getHorizontal();
    }

    @SuppressWarnings("deprecation")
    protected int getShapeIndex(BlockState state) {
        return this.SHAPE_INDEX_CACHE.computeIntIfAbsent(state, (statex) -> {
            int i = 0;
            if (statex.get(NORTH) != SideWall.NOTTING) {
                i |= getDirectionMask(Direction.NORTH);
            }

            if (statex.get(EAST) != SideWall.NOTTING) {
                i |= getDirectionMask(Direction.EAST);
            }

            if (statex.get(SOUTH) != SideWall.NOTTING) {
                i |= getDirectionMask(Direction.SOUTH);
            }

            if (statex.get(WEST) != SideWall.NOTTING) {
                i |= getDirectionMask(Direction.WEST);
            }

            return i;
        });
    }
}
