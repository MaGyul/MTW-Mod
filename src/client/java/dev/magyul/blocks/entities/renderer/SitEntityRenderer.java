package dev.magyul.blocks.entities.renderer;

import dev.magyul.entities.SitEntity;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class SitEntityRenderer extends EntityRenderer<SitEntity> {

    public SitEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public boolean shouldRender(SitEntity entity, Frustum frustum, double x, double y, double z) {
        return false;
    }

    @Override
    public Identifier getTexture(SitEntity entity) {
        return null;
    }
}
