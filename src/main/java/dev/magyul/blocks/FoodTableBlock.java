package dev.magyul.blocks;

import dev.magyul.blocks.entities.FoodTableBlockEntity;
import dev.magyul.registers.MTWProperties;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
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
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FoodTableBlock extends BlockWithEntity {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty LOCKED = Properties.LOCKED;
    public static final BooleanProperty FLUID_ROTATION = MTWProperties.FLUID_ROTATION;

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public FoodTableBlock(AbstractBlock.Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(LOCKED, false).with(FLUID_ROTATION, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(FACING).getAxis() == Direction.Axis.Z ? Shapes.FoodTableBlock.SHAPE_Z : Shapes.FoodTableBlock.SHAPE_X;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        var itemStack = player.getStackInHand(hand);
        if (itemStack.isEmpty()) {
            if (checkLocked(state, player)) return ActionResult.success(world.isClient);
            var blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof FoodTableBlockEntity && ((FoodTableBlockEntity) blockEntity).hasFood()) {
                if (player.isSneaking()) {
                    ((FoodTableBlockEntity) blockEntity).dropFood();
                    return ActionResult.success(world.isClient);
                }
                var stack = ((FoodTableBlockEntity) blockEntity).getStack();
                var component = stack.getItem().getFoodComponent();
                if (component != null && player.canConsume(component.isAlwaysEdible())) {
                    player.eatFood(world, stack.copy());
                    var finishStack = stack.getItem().finishUsing(stack.copy(), world, player);
                    if (!ItemStack.areItemsEqual(finishStack, stack)) {
                        ((FoodTableBlockEntity) blockEntity).dropFood(finishStack);
                    }
                    ((FoodTableBlockEntity) blockEntity).removeStack();
                    return ActionResult.success(world.isClient);
                }
            }
            return super.onUse(state, world, pos, player, hand, hit);
        } else {
            if (checkLocked(state, player)) return ActionResult.success(world.isClient);
            var component = itemStack.getItem().getFoodComponent();
            if (component != null) {
                var blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof FoodTableBlockEntity && !((FoodTableBlockEntity) blockEntity).hasFood()) {
                    ((FoodTableBlockEntity) blockEntity).setStack(
                            player.isCreative() ? itemStack.copyWithCount(1) : itemStack.split(1)
                    );
                    ((FoodTableBlockEntity) blockEntity).setPlaceRotation(player.getYaw());
                    return ActionResult.success(world.isClient);
                }
            }

            return ActionResult.PASS;
        }
    }

    private boolean checkLocked(BlockState state, PlayerEntity player) {
        if (state.get(LOCKED)) {
            player.sendMessage(Text.translatable("container.isLocked", Text.translatable(getTranslationKey())), true);
            player.playSound(SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return true;
        }
        return false;
    }

    @Override
    public boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
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

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }
}
