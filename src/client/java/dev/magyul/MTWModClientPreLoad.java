package dev.magyul;

import dev.magyul.mixin.client.accessor.IdentifierAccessor;
import dev.magyul.util.EnvironmentUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.toast.Toast;
import net.minecraft.util.Identifier;

import static dev.magyul.MTWModPreload.LOGGER;

public class MTWModClientPreLoad {

    public static void onPreLaunch() {
        if (!EnvironmentUtil.isClient()) return;
        try {
            Identifier toastTexture = Toast.TEXTURE;
            if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
                LOGGER.info("[b] Toast.TEXTURE = {}", toastTexture);
            }
            IdentifierAccessor accessor = (IdentifierAccessor) toastTexture;
            accessor.setNamespace(MTWMod.ID);
            accessor.setPath("textures/gui/toasts.png");
            if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
                LOGGER.info("[a] Toast.TEXTURE = {}", toastTexture);
            }
        } catch (Throwable t) {
            LOGGER.error("[Toast Field] TEXTURE 값 변경 실패", t);
        }
    }
}
