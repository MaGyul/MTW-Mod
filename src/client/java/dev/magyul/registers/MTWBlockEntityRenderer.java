package dev.magyul.registers;

import dev.magyul.blocks.entities.renderer.FoodTableBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class MTWBlockEntityRenderer {

    public static void register() {
        BlockEntityRendererFactories.register(MTWBlockEntityType.FOOD_TABLE_BLOCK_ENTITY, FoodTableBlockEntityRenderer::new);
    }
}
