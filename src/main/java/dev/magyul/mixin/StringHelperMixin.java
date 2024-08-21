package dev.magyul.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.util.StringHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StringHelper.class)
public class StringHelperMixin {
    @ModifyReturnValue(method = "isValidChar", at = @At("RETURN"))
    private static boolean isValidChar(boolean original, char chr) {
        return chr >= ' ' && chr != 127;
    }
}
