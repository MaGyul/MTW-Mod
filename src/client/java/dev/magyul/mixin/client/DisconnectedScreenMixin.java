package dev.magyul.mixin.client;

import dev.magyul.MTWMod;
import dev.magyul.ServerPingPong;
import dev.magyul.util.ClientUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DisconnectedScreen.class)
public class DisconnectedScreenMixin extends Screen {
    @Shadow @Final private Screen parent;

    @Shadow @Final private Text reason;

    private DisconnectedScreenMixin() {
        super(Text.empty());
    }

    @Unique
    private void showToast(Text title, Text reason) {
        var toast = client.getToastManager();
        try {
            toast.add(SystemToast.create(client, ClientUtil.MTW_TOAST, title, reason));
        } catch (Exception ex) {
            MTWMod.LOGGER.error("Error: ", ex);
        }
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    private void init(CallbackInfo cb) {
        cb.cancel();
        ClientUtil.cs = null;
        ServerPingPong.serverJoined = false;
        client.setScreen(parent instanceof TitleScreen ? parent : new TitleScreen());
        ClientUtil.setTimeout(() -> showToast(title, reason), 100);
    }
}
