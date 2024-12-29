package dev.magyul.mixin;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    @Inject(method = "onPlayerCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getCount()I"), cancellable = true)
    private void onPlayerCollision(PlayerEntity player, CallbackInfo cb) {
        var serverPlayer = (ServerPlayerEntity) player;
        if (serverPlayer.interactionManager.getGameMode() == GameMode.ADVENTURE) {
            cb.cancel();
        }
    }
}
