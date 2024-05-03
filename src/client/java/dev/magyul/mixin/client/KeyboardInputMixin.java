package dev.magyul.mixin.client;

import dev.magyul.registers.MTWDataComponentTypes;
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
            var inventory = client.player.getInventory();
            var stack = inventory.getArmorStack(3);
            if (!stack.isEmpty() && stack.getOrDefault(MTWDataComponentTypes.IS_CARRY, false)) {
                return true;
            }
        }
        return instance.isPressed();
    }
}
