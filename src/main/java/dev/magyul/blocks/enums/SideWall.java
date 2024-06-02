package dev.magyul.blocks.enums;

import dev.magyul.blocks.SpruceEarthWall;
import dev.magyul.registers.MTWBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.SlabType;
import net.minecraft.util.StringIdentifiable;

import java.util.function.Predicate;
import java.util.function.Supplier;

public enum SideWall implements StringIdentifiable {
    NOTTING(() -> null),
    EARTH_WALL(() -> MTWBlocks.EARTH_WALL),
    SPRUCE_EARTH_WALL_TOP(() -> MTWBlocks.SPRUCE_EARTH_WALL, (state) -> state.get(SpruceEarthWall.TYPE) == SlabType.TOP),
    SPRUCE_EARTH_WALL_BOTTOM(() -> MTWBlocks.SPRUCE_EARTH_WALL, (state) -> state.get(SpruceEarthWall.TYPE) == SlabType.BOTTOM),
    SPRUCE_EARTH_WALL_FRAME(() -> MTWBlocks.SPRUCE_EARTH_WALL_FRAME),
    SPRUCE_PLATE_WALL(() -> MTWBlocks.SPRUCE_PLATE_WALL),
    SPRUCE_PLATE_WALL_FRAME(() -> MTWBlocks.SPRUCE_PLATE_WALL_FRAME);

    private final Supplier<Block> block;
    private final Predicate<BlockState> filter;

    SideWall(Supplier<Block> block) {
        this(block, (state) -> true);
    }

    SideWall(Supplier<Block> block, Predicate<BlockState> filter) {
        this.block = block;
        this.filter = filter;
    }

    public static SideWall parse(BlockState state) {
        for (SideWall value : values()) {
            var block = value.block.get();
            if (block != null && state.isOf(block) && value.filter.test(state)) {
                return value;
            }
        }
        return NOTTING;
    }

    public String toString() {
        return this.asString();
    }

    public String asString() {
        return name().toLowerCase();
    }
}
