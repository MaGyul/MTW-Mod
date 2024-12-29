package dev.magyul.mixin.client;

import dev.magyul.util.ClientUtil;
import dev.magyul.util.ConnectServer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.QuickPlay;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(QuickPlay.class)
public class QuickPlayMixin {

    @Inject(method = "startMultiplayer", at = @At("HEAD"), cancellable = true)
    private static void startMultiplayer(MinecraftClient client, String serverAddress, CallbackInfo cb) {
        cb.cancel();
        client.setScreen(new TitleScreen());
        if (ClientUtil.checkTest()) return;

        ClientUtil.cs = ConnectServer.startConnecting(client, new TitleScreen(), ClientUtil.mtw_address, ClientUtil.mtw_info);
        client.setScreen(new TitleScreen());
    }
}
