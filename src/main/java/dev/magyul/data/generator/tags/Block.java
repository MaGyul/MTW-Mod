package dev.magyul.data.generator.tags;

import dev.magyul.registers.MTWBlocks;
import dev.magyul.registers.MTWTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class Block extends FabricTagProvider.BlockTagProvider {
    public Block(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(MTWTags.CONNECTED_WALLS)
                .add(Blocks.SPRUCE_LOG)
                .add(Blocks.SPRUCE_WOOD)
                .add(Blocks.STONE_BRICKS)
                .add(Blocks.CRACKED_STONE_BRICKS)
                .add(Blocks.MOSSY_STONE_BRICKS)
                .add(MTWBlocks.SPRUCE_WALL_PILLAR);

        getOrCreateTagBuilder(MTWTags.EARTHS)
                .add(MTWBlocks.EARTH_WALL)
                .add(MTWBlocks.SPRUCE_EARTH_WALL_FRAME)
                .add(MTWBlocks.SPRUCE_EARTH_WALL);
        getOrCreateTagBuilder(MTWTags.PLATES)
                .add(MTWBlocks.SPRUCE_PLATE_WALL_FRAME)
                .add(MTWBlocks.SPRUCE_PLATE_WALL);

        getOrCreateTagBuilder(MTWTags.WALLS)
                .addTag(MTWTags.EARTHS)
                .addTag(MTWTags.PLATES);

        getOrCreateTagBuilder(MTWTags.SITTINGS)
                .add(MTWBlocks.SPRUCE_PLANKS_CHAIR);
    }
}
