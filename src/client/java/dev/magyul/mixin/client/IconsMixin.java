package dev.magyul.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.magyul.MTWModClient;
import net.minecraft.client.util.Icons;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.io.InputStream;

@Mixin(Icons.class)
public class IconsMixin {

    @ModifyReturnValue(method = "getIcon", at = @At("RETURN"))
    private InputSupplier<InputStream> getIcon(InputSupplier<InputStream> original, ResourcePack resourcePack, String file) {
        var url = MTWModClient.class.getClassLoader().getResource("icon/" + file);
        if (url != null) {
            return url::openStream;
        }
        return original;
    }
}
