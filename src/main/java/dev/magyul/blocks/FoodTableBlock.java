package dev.magyul.blocks;

import com.mojang.serialization.MapCodec;
import dev.magyul.blocks.entities.FoodTableBlockEntity;
import dev.magyul.registers.MTWProperties;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FoodTableBlock extends BlockWithEntity {
    public static final MapCodec<StandardBlock> CODEC = createCodec(StandardBlock::new);
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty LOCKED = Properties.LOCKED;
    public static final BooleanProperty FLUID_ROTATION = MTWProperties.FLUID_ROTATION;

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public FoodTableBlock(AbstractBlock.Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(LOCKED, false).with(FLUID_ROTATION, false));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(FACING).getAxis() == Direction.Axis.Z ? Shapes.FoodTableBlock.SHAPE_Z : Shapes.FoodTableBlock.SHAPE_X;
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (checkLocked(state, player)) return ItemActionResult.success(world.isClient);
        var component = stack.get(DataComponentTypes.FOOD);
        if (component != null) {
            var blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof FoodTableBlockEntity && !((FoodTableBlockEntity) blockEntity).hasFood()) {
                ((FoodTableBlockEntity) blockEntity).setStack(
                        player.isCreative() ? stack.copyWithCount(1) : stack.split(1)
                );
                ((FoodTableBlockEntity) blockEntity).setPlaceRotation(player.getYaw());
                return ItemActionResult.success(world.isClient);
            }
        }

        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (checkLocked(state, player)) return ActionResult.success(world.isClient);
        var blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof FoodTableBlockEntity && ((FoodTableBlockEntity) blockEntity).hasFood()) {
            if (player.isSneaking()) {
                ((FoodTableBlockEntity) blockEntity).dropFood();
                return ActionResult.success(world.isClient);
            }
            var stack = ((FoodTableBlockEntity) blockEntity).getStack();
            var component = stack.get(DataComponentTypes.FOOD);
            if (component != null && player.canConsume(component.canAlwaysEat())) {
                player.eatFood(world, stack.copy(), component);
                var finishStack = stack.getItem().finishUsing(stack.copy(), world, player);
                if (!ItemStack.areItemsEqual(finishStack, stack)) {
                    ((FoodTableBlockEntity) blockEntity).dropFood(finishStack);
                }
                ((FoodTableBlockEntity) blockEntity).emptyStack();
                return ActionResult.success(world.isClient);
            }
        }
        return super.onUse(state, world, pos, player, hit);
    }

    private boolean checkLocked(BlockState state, PlayerEntity player) {
        if (state.get(LOCKED)) {
            player.sendMessage(Text.translatable("container.isLocked", Text.translatable(getTranslationKey())), true);
            player.playSoundToPlayer(SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return true;
        }
        return false;
    }

    @Override
    protected boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        var blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof FoodTableBlockEntity && !state.isOf(newState.getBlock())) {
            if (((FoodTableBlockEntity) blockEntity).hasFood()){
                ((FoodTableBlockEntity) blockEntity).dropFood();
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FoodTableBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, LOCKED, FLUID_ROTATION);
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }
}
