package dev.magyul.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
    @ModifyReturnValue(method = "getArmPose", at = @At("RETURN"))
    private static BipedEntityModel.ArmPose getArmPose(BipedEntityModel.ArmPose original, AbstractClientPlayerEntity player, Hand hand) {
        var inventory = player.getInventory();
        var head = inventory.getArmorStack(3);
        if (!head.isEmpty() && head.getOrCreateNbt().getBoolean("mtw:carry")) {
            return BipedEntityModel.ArmPose.CROSSBOW_HOLD;
        }
        return original;
    }
}
