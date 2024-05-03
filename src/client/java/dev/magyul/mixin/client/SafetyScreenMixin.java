package dev.magyul.mixin.client;

import dev.magyul.util.ClientUtil;
import dev.magyul.util.ConnectServer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WarningScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
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

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "method_57752", at = @At("HEAD"), cancellable = true)
    private void proceedButton(ButtonWidget buttonWidget, CallbackInfo cb) {
        cb.cancel();
        if (this.checkbox.isChecked()) {
            this.client.options.skipMultiplayerWarning = true;
            this.client.options.write();
        }

        client.setScreen(parent);
        ClientUtil.setTimeout(() ->
                ClientUtil.cs = ConnectServer.startConnecting(client, ClientUtil.mtw_address, ClientUtil.mtw_info), 100);
    }
}
