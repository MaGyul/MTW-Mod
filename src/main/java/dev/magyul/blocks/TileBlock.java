package dev.magyul.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class TileBlock extends HorizontalFacingBlock {
    public static final MapCodec<MoreSlabBlock> CODEC = createCodec(MoreSlabBlock::new);
    protected static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 6, 16);

    @Override
    public MapCodec<? extends MoreSlabBlock> getCodec() {
        return CODEC;
    }

    public TileBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }
}
