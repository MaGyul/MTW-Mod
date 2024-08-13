package dev.magyul.registers;

import dev.magyul.blocks.entities.renderer.SitEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class MTWEntityRenderer {

    public static void register() {
        EntityRendererRegistry.register(MTWEntityType.SIT, SitEntityRenderer::new);
    }
}
