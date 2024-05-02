package dev.magyul.render;

import dev.magyul.blocks.entities.SignBoardEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SignBoardEntityRenderer implements BlockEntityRenderer<SignBoardEntity> {
    private static final String STICK = "stick";
    private static final int GLOWING_BLOCK_COLOR = -988212;
    private static final int RENDER_DISTANCE = MathHelper.square(16);
    private static final float SCALE = 0.6666667F;
    private static final Vec3d TEXT_OFFSET = new Vec3d(0.0, 0.3333333432674408, 0.046666666865348816);

    public SignBoardEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        
    }

    @Override
    public void render(SignBoardEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

    }
}
