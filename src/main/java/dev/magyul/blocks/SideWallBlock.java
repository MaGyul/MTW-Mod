package dev.magyul.blocks;

import com.mojang.serialization.MapCodec;
import dev.magyul.registers.MTWTags;
import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.LeadItem;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.Objects;

public class SideWallBlock extends HorizontalConnectingBlock {
    public static final MapCodec<SideWallBlock> CODEC = createCodec(SideWallBlock::new);
    private final VoxelShape[] cullingShapes;

    @Override
    public MapCodec<SideWallBlock> getCodec() {
        return CODEC;
    }

    public SideWallBlock(AbstractBlock.Settings settings) {
        super(3.0F, 3.0F, 16.0F, 16.0F, 16.0F, settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(WATERLOGGED, false));
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

    @Override
    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    public boolean canConnect(BlockState state, boolean ignoredNeighborIsFullSquare, Direction dir) {
        Block block = state.getBlock();
        boolean bl = this.canConnectToWall(state);
        boolean bl3 = block instanceof WindowBlock && WindowBlock.canWallConnect(state, dir);
        if (state.isAir()) return false;
        return
//                !cannotConnect(state) && neighborIsFullSquare ||
                bl || bl3;
    }

    private boolean canConnectToWall(BlockState state) {
//        return state.isIn(BlockTags.FENCES) && state.isIn(BlockTags.WOODEN_FENCES) == this.getDefaultState().isIn(BlockTags.WOODEN_FENCES);
        if (state.isIn(MTWTags.CONNECTED_WALLS)) return true;
        if (state.isIn(MTWTags.WALLS)) {
            if (state.isIn(MTWTags.PLATES)) {
                return state.isIn(MTWTags.PLATES) == this.getDefaultState().isIn(MTWTags.PLATES);
            }
            if (state.isIn(MTWTags.EARTHS)) {
                return state.isIn(MTWTags.EARTHS) == this.getDefaultState().isIn(MTWTags.EARTHS);
            }
        }
        return false;
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
                .with(NORTH, this.canConnect(blockState, blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.SOUTH), Direction.SOUTH))
                .with(EAST, this.canConnect(blockState2, blockState2.isSideSolidFullSquare(blockView, blockPos3, Direction.WEST), Direction.WEST))
                .with(SOUTH, this.canConnect(blockState3, blockState3.isSideSolidFullSquare(blockView, blockPos4, Direction.NORTH), Direction.NORTH))
                .with(WEST, this.canConnect(blockState4, blockState4.isSideSolidFullSquare(blockView, blockPos5, Direction.EAST), Direction.EAST))
                .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        if (direction.getAxis().getType() == Direction.Type.HORIZONTAL) {
            return state.with(
                    FACING_PROPERTIES.get(direction),
                    this.canConnect(
                            neighborState,
                            neighborState.isSideSolidFullSquare(world, neighborPos, direction.getOpposite()),
                            direction.getOpposite()));
        } else {
            return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
    }
}
