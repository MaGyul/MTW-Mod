package dev.magyul.mixin.client;

import dev.magyul.util.ClientUtil;
import dev.magyul.util.ConnectServer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MultiplayerWarningScreen.class)
public abstract class SafetyScreenMixin {
    @Shadow @Final private Screen parent;

    @Redirect(method = "method_57752", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"))
    private void proceedButton(MinecraftClient client, Screen screen) {
        client.setScreen(parent);
        ClientUtil.setTimeout(() ->
                ClientUtil.cs = ConnectServer.startConnecting(client, ClientUtil.mtw_address, ClientUtil.mtw_info), 100);
    }
}
