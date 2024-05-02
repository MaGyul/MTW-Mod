package dev.magyul.mixin.client;

import dev.magyul.MTWMod;
import dev.magyul.MTWModClient;
import net.minecraft.client.util.Icons;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStream;

@Mixin(Icons.class)
public class IconsMixin {

    @Inject(method = "getIcon", at = @At("HEAD"), cancellable = true)
    private void getIcon(ResourcePack pack, String file, CallbackInfoReturnable<InputSupplier<InputStream>> cb) {
        var url = MTWModClient.class.getClassLoader().getResource("icon/" + file);
        if (url == null) {
            MTWMod.LOGGER.info("icon/{} not found", file);
        } else {
            cb.setReturnValue(url::openStream);
        }
    }
}
