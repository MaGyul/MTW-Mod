package dev.magyul.blocks;

import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

import java.util.List;
import java.util.function.ToIntFunction;

public class ErrorBlock extends Block {
    public static final MapCodec<ErrorBlock> CODEC = createCodec(ErrorBlock::new);
    public static final int MIN_LEVEL = 0;
    public static final IntProperty LEVEL = Properties.LEVEL_15;
    public static final ToIntFunction<BlockState> LIGHT_EMISSION = (state) -> state.get(LEVEL);
    public static Function4<ItemStack, Item.TooltipContext, List<Text>, TooltipType, Void> clientCallback = null;

    @Override
    protected MapCodec<? extends Block> getCodec() {
        return CODEC;
    }

    public ErrorBlock(Settings settings) {
        super(settings);
        setDefaultState(stateManager.getDefaultState().with(LEVEL, MIN_LEVEL));
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        tooltip.add(Text.translatable("item.mtwmod.error_block_light", getLightOnStack(stack)));

        if (clientCallback != null) {
            clientCallback.apply(stack, context, tooltip, options);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return setLightOnStack(super.getPickStack(world, pos, state), state.get(LEVEL));
    }

    public static int getLightOnStack(ItemStack item) {
        var level = 0;
        var data = item.get(DataComponentTypes.BLOCK_STATE);
        if (data != null) {
            var getLevel = data.getValue(LEVEL);
            if (getLevel != null) level = getLevel;
        }

        return level;
    }

    public static ItemStack setLightOnStack(ItemStack item, int lightLevel) {
        if (lightLevel != MIN_LEVEL) {
            item.set(DataComponentTypes.BLOCK_STATE, BlockStateComponent.DEFAULT.with(LEVEL, lightLevel));
        } else {
            var data = item.get(DataComponentTypes.BLOCK_STATE);
            if (data != null) {
                data.properties().remove(LEVEL.getName());
                item.set(DataComponentTypes.BLOCK_STATE, data);
            }
        }

        return item;
    }

    public static int nextLightOnStack(PlayerEntity player, ItemStack item, boolean remove) {
        var level = getLightOnStack(item);
        if (remove) {
            if (level <= 0) {
                level = 15;
            } else {
                level -= 1;
            }
        } else {
            if (level >= 15) {
                level = 0;
            } else {
                level += 1;
            }
        }
        setLightOnStack(item, level);
        player.sendMessage(Text.translatable("item.mtwmod.error_block_light", level), true);
        return level;
    }
}
