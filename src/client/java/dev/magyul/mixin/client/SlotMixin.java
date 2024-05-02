package dev.magyul.mixin.client;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public class SlotMixin {
    @Shadow @Final public Inventory inventory;

    @Shadow @Final private int index;

    @Inject(method = "isEnabled", at = @At("HEAD"), cancellable = true)
    private void isEnabled(CallbackInfoReturnable<Boolean> cb) {
        if (inventory instanceof PlayerInventory playerInventory) {
            var player = (ClientPlayerEntity) playerInventory.player;
            var entry = player.getPlayerListEntry();
            if (entry != null && entry.getGameMode() == GameMode.ADVENTURE) {
                if (!PlayerInventory.isValidHotbarIndex(index)) {
                    cb.setReturnValue(false);
                }
            }
        }
    }
}
