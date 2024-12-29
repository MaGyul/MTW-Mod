package dev.magyul.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {

    @ModifyArgs(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", ordinal = 0))
    private void injected(Args args) {
        AbstractClientPlayerEntity player = args.get(0);
        var inventory = player.getInventory();
        var head = inventory.getArmorStack(3);
        if (!head.isEmpty() && head.getOrCreateNbt().getBoolean("mtw:carry")) {
            args.set(5, head.copy());
            args.set(6, 0f);
        }
    }

    @WrapOperation(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", ordinal = 1))
    private void renderFirstPersonItem$renderItem(HeldItemRenderer instance, LivingEntity entity, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Operation<Void> original) {
        if (stack.getOrCreateNbt().getBoolean("mtw:carry")) {
//            matrices.translate(-.541864F, .25F, .0F);
            matrices.translate(-.56F, .25F, .0F);
//            matrices.translate(.0f, .5f, -1f);
            matrices.scale(1.2f, 1.2f, 1.2f);
            if (stack.getItem() instanceof BlockItem) {
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-45.0f));
            } else {
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-25.0f));
            }
        }
        original.call(instance, entity, stack, renderMode, leftHanded, matrices, vertexConsumers, light);
    }
}
