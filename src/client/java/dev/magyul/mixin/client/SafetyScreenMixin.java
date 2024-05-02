package dev.magyul.mixin.client;

import dev.magyul.ServerPingPong;
import dev.magyul.util.ClientUtil;
import dev.magyul.util.ConnectServer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WarningScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerWarningScreen.class)
public abstract class SafetyScreenMixin extends WarningScreen {
    @Shadow @Final private static Text HEADER;

    @Shadow @Final private static Text MESSAGE;

    @Shadow @Final private static Text CHECK_MESSAGE;

    @Shadow @Final private static Text NARRATED_TEXT;

    @Shadow @Final private Screen parent;

    private SafetyScreenMixin() {
        super(HEADER, MESSAGE, CHECK_MESSAGE, NARRATED_TEXT);
    }

    @Inject(method = "initButtons", at = @At("HEAD"), cancellable = true)
    private void initButtons(int yOffset, CallbackInfo cb) {
        cb.cancel();
        addDrawableChild(ButtonWidget.builder(ScreenTexts.PROCEED, (button) -> {
            if (checkbox.isChecked()) {
                client.options.skipMultiplayerWarning = true;
                client.options.write();
            }

            client.setScreen(parent);
            ClientUtil.setTimeout(() ->
                    ClientUtil.cs = ConnectServer.startConnecting(client, ClientUtil.mtw_address, ClientUtil.mtw_info), 100);
        }).dimensions(width / 2 - 155, 100 + yOffset, 150, 20).build());
        addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, (button) -> client.setScreen(parent)).dimensions(width / 2 - 155 + 160, 100 + yOffset, 150, 20).build());
    }
}
