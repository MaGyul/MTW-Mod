package dev.magyul.blocks;

import com.mojang.serialization.MapCodec;
import dev.magyul.registers.MTWBlocks;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class MoreSlabBlock extends HorizontalFacingBlock {
    public static final MapCodec<MoreSlabBlock> CODEC = createCodec(MoreSlabBlock::new);
    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 13.0, 0.0, 16.0, 16.0, 16.0);

    public MapCodec<? extends MoreSlabBlock> getCodec() {
        return CODEC;
    }

    public MoreSlabBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }
}
