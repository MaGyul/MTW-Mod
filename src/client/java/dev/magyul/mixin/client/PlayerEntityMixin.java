package dev.magyul.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @ModifyReturnValue(method = "getName", at = @At("RETURN"))
    private Text getName(Text original) {
        if (This() instanceof AbstractClientPlayerEntity player) {
            if (MinecraftClient.getInstance().getNetworkHandler() == null) return original;
            var entry = player.getPlayerListEntry();
            if (entry != null) {
                return entry.getDisplayName();
            }
        }
        return original;
    }

    @Unique
    private PlayerEntity This() {
        return (PlayerEntity) (Object) this;
    }
}
