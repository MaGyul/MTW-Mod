package dev.magyul.mixin.pehkui;

import net.fabricmc.loader.api.MappingResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import virtuoel.pehkui.util.ReflectionUtils;

@Mixin(value = ReflectionUtils.class, remap = false)
public class ReflectionUtilsMixin {

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/fabricmc/loader/api/MappingResolver;mapFieldName(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", ordinal = 3), remap = false)
    private static String mapFieldName(MappingResolver mappingResolver, String namespace, String owner, String name, String descriptor) {
        return mappingResolver.mapFieldName(namespace, owner, "getHoldingEntity", descriptor);
    }
}