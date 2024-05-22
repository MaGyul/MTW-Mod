package dev.magyul.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @ModifyReturnValue(method = "getPlayerListName", at = @At("RETURN"))
    private Text getPlayerListName(Text original) {
        return ((ServerPlayerEntity) (Object) this).getName();
    }
}
