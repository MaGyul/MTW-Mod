package dev.magyul.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.magyul.util.ClientUtil;
import net.fabricmc.fabric.impl.networking.client.ClientNetworkingImpl;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientNetworkingImpl.class)
public class ClientNetworkingImplMixin {

    @ModifyReturnValue(method = "getLoginConnection", at = @At("RETURN"))
    private static ClientConnection returnCustomConnection(ClientConnection original) {
        if (original == null && ClientUtil.cs != null) {
            return ClientUtil.cs.connection;
        }

        return original;
    }
}
