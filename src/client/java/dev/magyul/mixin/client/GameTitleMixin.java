package dev.magyul.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.magyul.MTWMod;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Window;
import net.minecraft.server.integrated.IntegratedServer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class GameTitleMixin {

    @Shadow @Nullable public abstract ClientPlayNetworkHandler getNetworkHandler();

    @Shadow @Nullable public abstract ServerInfo getCurrentServerEntry();

    @Shadow @Nullable private IntegratedServer server;

    @ModifyReturnValue(method = "getWindowTitle", at = @At("RETURN"))
    private String getWindowTitle(String original) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            return createTitle();
        }
        if (I18n.hasTranslation("game.title")) {
            return I18n.translate("game.title");
        }
        return "Cheonnyeon Story";
    }

    @Unique
    private String createTitle() {
        StringBuilder stringBuilder = new StringBuilder(getTitle());
        stringBuilder.append(" ");
        stringBuilder.append(MTWMod.VERSION);
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.getNetworkHandler();
        if (clientPlayNetworkHandler != null && clientPlayNetworkHandler.getConnection().isOpen()) {
            stringBuilder.append(" - ");
            ServerInfo serverInfo = this.getCurrentServerEntry();
            if (this.server != null && !this.server.isRemote()) {
                stringBuilder.append(I18n.translate("title.singleplayer"));
            } else if (serverInfo != null && serverInfo.isRealm()) {
                stringBuilder.append(I18n.translate("title.multiplayer.realms"));
            } else if (this.server == null && (serverInfo == null || !serverInfo.isLocal())) {
                stringBuilder.append(I18n.translate("title.multiplayer.other"));
            } else {
                stringBuilder.append(I18n.translate("title.multiplayer.lan"));
            }
        }

        return stringBuilder.toString();
    }

    @Unique
    private String getTitle() {
        if (I18n.hasTranslation("game.title")) {
            return I18n.translate("game.title");
        }
        return "Cheonnyeon Story";
    }
}
