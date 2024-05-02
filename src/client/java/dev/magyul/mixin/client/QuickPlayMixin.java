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

        /*
        if (!pinged() && !mtw_info.online) {
            mtw_info.online = true;
            mtw_info.ping = -2L;
            mtw_info.label = ScreenTexts.EMPTY;
            mtw_info.playerCountLabel = ScreenTexts.EMPTY;
            ServerPingPong.startPinging();
        }

        a
         */

        ClientUtil.cs = ConnectServer.startConnecting(client, ClientUtil.mtw_address, ClientUtil.mtw_info);
    }
    /*
        final Timer[] timer = new Timer[1];
        timer[0] = ClientUtil.setInterval(() -> {
            if (pinged() && mtw_info.ping >= 0L) {
                timer[0].cancel();
            }
        }, 100);
    }

    @Unique
    private static boolean pinged() {
        return ClientUtil.mtw_info.online && ClientUtil.mtw_info.ping != -2L;
    }
     */
}
