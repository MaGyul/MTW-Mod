package dev.magyul.blocks;

import com.mojang.serialization.MapCodec;
import dev.magyul.MTWMod;
import dev.magyul.blocks.entities.StandardBlockEntity;
import dev.magyul.blocks.enums.StandardStatus;
import dev.magyul.registers.MTWProperties;
import dev.magyul.registers.MTWTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.ToIntFunction;

public class StandardBlock extends BlockWithEntity {
    public static final MapCodec<StandardBlock> CODEC = createCodec(StandardBlock::new);
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    private static final EnumProperty<StandardStatus> STANDARD_STATUS = MTWProperties.STANDARD_STATUS;
    protected static final VoxelShape SHAPE = Block.createCuboidShape(4, 0, 4, 12, 17, 12);

    public static final ToIntFunction<BlockState> LUMINANCE = (state) -> state.get(STANDARD_STATUS) == StandardStatus.ON ? 15 : 0;

    @Override
    public MapCodec<? extends StandardBlock> getCodec() {
        return CODEC;
    }

    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public StandardBlock(AbstractBlock.Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(STANDARD_STATUS, StandardStatus.NOTTING));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        var nbt = itemStack.getOrDefault(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.DEFAULT);
        if (nbt.contains("LanternItem")) {
            world.setBlockState(pos, state.with(STANDARD_STATUS, StandardStatus.OFF), 2);
        }
    }

    private boolean drop(World world, BlockPos pos) {
        var be = world.getBlockEntity(pos);
        if (be instanceof StandardBlockEntity sbe) {
            sbe.drop();
            world.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return true;
        }
        return false;
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        var status = state.get(STANDARD_STATUS);

        if (status == StandardStatus.NOTTING && stack.isIn(MTWTags.LANTERNS)) {
            var clone = stack.copyWithCount(1);
            if (!player.isCreative()) stack.split(1);
            var blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof StandardBlockEntity sbe) {
                sbe.setStack(clone);
            }
            world.setBlockState(pos, state.with(STANDARD_STATUS, StandardStatus.OFF), 3);
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_LANTERN_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return ItemActionResult.success(world.isClient);
        }

        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (state.get(STANDARD_STATUS) != StandardStatus.NOTTING) {
            if (player.isSneaking() && drop(world, pos)) {
                if (state.get(STANDARD_STATUS) == StandardStatus.ON) {
                    player.damage(player.getDamageSources().inFire(), 1);
                }

                world.setBlockState(pos, state.with(STANDARD_STATUS, StandardStatus.NOTTING), 3);
                return ActionResult.success(world.isClient);
            }
            world.setBlockState(pos, state.with(STANDARD_STATUS, state.get(STANDARD_STATUS) == StandardStatus.OFF
                    ? StandardStatus.ON : StandardStatus.OFF), 3);
            return ActionResult.success(world.isClient);
        }
        return ActionResult.CONSUME;
    }

    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity var7 = world.getBlockEntity(pos);
            if (var7 instanceof StandardBlockEntity sbe) {
                sbe.drop();
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new StandardBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, STANDARD_STATUS);
    }

    @Override
    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return state.get(STANDARD_STATUS) == StandardStatus.ON ? 15 : 0;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }
}
