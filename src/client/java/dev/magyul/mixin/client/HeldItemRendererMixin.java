package dev.magyul.mixin.client;

import dev.magyul.registers.MTWDataComponentTypes;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Objects;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {

    @ModifyArgs(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", ordinal = 0))
    private void injected(Args args) {
        AbstractClientPlayerEntity player = args.get(0);
        var inventory = player.getInventory();
        var head = inventory.getArmorStack(3);
        if (!head.isEmpty() && head.getOrDefault(MTWDataComponentTypes.IS_CARRY, false)) {
            args.set(5, head.copy());
            args.set(6, 0f);
        }
    }

    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", ordinal = 1, shift = At.Shift.BEFORE))
    private void renderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch,
                                       Hand hand, float swingProgress, ItemStack item, float equipProgress,
                                       MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (item.getOrDefault(MTWDataComponentTypes.IS_CARRY, false)) {
//            matrices.translate(-.541864F, .25F, .0F);
            matrices.translate(-.56F, .25F, .0F);
//            matrices.translate(.0f, .5f, -1f);
            matrices.scale(1.2f, 1.2f, 1.2f);
            if (item.getItem() instanceof BlockItem) {
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-45.0f));
            } else {
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-25.0f));
            }
        }
    }
}
