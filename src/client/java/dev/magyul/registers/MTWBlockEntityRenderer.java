package dev.magyul.registers;

import dev.magyul.render.SignBoardEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class MTWBlockEntityRenderer {

    public static void register() {
        BlockEntityRendererFactories.register(MTWBlockEntities.SIGN_BOARD, SignBoardEntityRenderer::new);
    }
}
