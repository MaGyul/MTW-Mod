package dev.magyul.blocks.entities.renderer;

import dev.magyul.blocks.FoodTableBlock;
import dev.magyul.blocks.entities.FoodTableBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class FoodTableBlockEntityRenderer implements BlockEntityRenderer<FoodTableBlockEntity> {
    private final ItemRenderer itemRenderer;
    private final EntityRenderDispatcher entityRenderDispatcher;

    public FoodTableBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.itemRenderer = ctx.getItemRenderer();
        this.entityRenderDispatcher = ctx.getEntityRenderDispatcher();
    }

    @Override
    public void render(FoodTableBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entityRenderDispatcher.camera != null) {
            entity.getController().tick(entityRenderDispatcher.camera.getPos());
        }

        var state = entity.getCachedState();
        var world = entity.getWorld();
        if (world != null && entity.hasFood()) {
            matrices.push();
            // Calculate the current offset in the y value
//            var offset = Math.sin((world.getTime() + tickDelta) / 8.0) / 4.0;
            // Move the item
            matrices.translate(.5, .588, .5);

            // Rotate the item
            if (state.get(FoodTableBlock.FLUID_ROTATION)) {
                var controller = entity.getController();
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(tickDelta, controller.currentAngle, controller.nextAngle)));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
            } else {
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-entity.getPlaceRotation()));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
            }
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));

            // Scale the item
            matrices.scale(.8f, .8f, .8f);

            var lightAbove = WorldRenderer.getLightmapCoordinates(world, entity.getPos().up());
            itemRenderer.renderItem(entity.getStack(), ModelTransformationMode.FIXED, lightAbove, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, world, 0);

            // Mandatory call after GL calls
            matrices.pop();
        }
    }
}
