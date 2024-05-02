package dev.magyul.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.BlockItem;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin {

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 5))
    private boolean tick(KeyBinding instance) {
        var client = MinecraftClient.getInstance();
        if (client.player != null) {
            var entry = client.player.getPlayerListEntry();
            if (entry != null && entry.getGameMode() == GameMode.ADVENTURE) {
                var inventory = client.player.getInventory();
                var stack = inventory.getArmorStack(3);
                if (!stack.isEmpty() && stack.getItem() instanceof BlockItem) {
                    return true;
                }
            }
        }
        return instance.isPressed();
    }
}
