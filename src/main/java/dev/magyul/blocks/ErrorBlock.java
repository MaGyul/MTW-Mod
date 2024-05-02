package dev.magyul.blocks;

import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.ToIntFunction;

public class ErrorBlock extends Block {
    public static final MapCodec<ErrorBlock> CODEC = createCodec(ErrorBlock::new);
    public static final int MIN_LEVEL = 0;
    public static final IntProperty LEVEL = Properties.LEVEL_15;
    public static final ToIntFunction<BlockState> LIGHT_EMISSION = (state) -> state.get(LEVEL);
    public static Function4<ItemStack, @Nullable BlockView, List<Text>, TooltipContext, Void> clientCallback = null;

    @Override
    protected MapCodec<? extends Block> getCodec() {
        return CODEC;
    }

    public ErrorBlock(Settings settings) {
        super(settings);
        setDefaultState(stateManager.getDefaultState().with(LEVEL, MIN_LEVEL));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        var tag = stack.getOrCreateNbt();
        if (tag.contains("BlockStateTag")) {
            tag = tag.getCompound("BlockStateTag");
            if (tag.contains(LEVEL.getName())) {
                var level = tag.getInt(LEVEL.getName());
                tooltip.add(Text.translatable("item.mtwmod.error_block_light", level));
            }
        } else {
            tooltip.add(Text.translatable("item.mtwmod.error_block_light", 0));
        }

        if (clientCallback != null) {
            clientCallback.apply(stack, world, tooltip, options);
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
        var tag = item.getOrCreateNbt();
        if (tag.contains("BlockStateTag")) {
            tag = tag.getCompound("BlockStateTag");
            if (tag.contains(LEVEL.getName())) {
                level = tag.getInt(LEVEL.getName());
            }
        }

        return level;
    }

    public static ItemStack setLightOnStack(ItemStack item, int lightLevel) {
        if (lightLevel != MIN_LEVEL) {
            NbtCompound tag = new NbtCompound();
            tag.putInt(LEVEL.getName(), lightLevel);
            item.setSubNbt("BlockStateTag", tag);
        } else {
            item.removeSubNbt("BlockStateTag");
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
