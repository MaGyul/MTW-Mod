package dev.magyul;

import dev.magyul.util.EnvironmentUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.LanguageAdapter;
import net.fabricmc.loader.api.LanguageAdapterException;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.tools.obfuscation.mirror.FieldHandle;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class MTWModPreload implements PreLaunchEntrypoint {
    public static final Logger LOGGER = LoggerFactory.getLogger(MTWModPreload.class);

    @Override
    public void onPreLaunch() {
        if (EnvironmentUtil.isClient()) {
            try {
                Class<?> clientPreload = Class.forName("dev.magyul.MTWModClientPreLoad");
                clientPreload.getDeclaredMethod("onPreLaunch").invoke(null);
            } catch (Throwable e) {
                LOGGER.error("error", e);
            }
        }
        final var lookup = MethodHandles.lookup();
        try {
            final Class<?> alsClass = ClassLoader.getSystemClassLoader().loadClass(
                    "io.github.gaming32.modloadingscreen.ActualLoadingScreen"
            );

            var setTitleFromMetadata = lookup.findStatic(
                    alsClass, "setTitleFromMetadata",
                    MethodType.methodType(void.class, String.class, String.class, String.class)
            );
            var mtwmod = FabricLoader.getInstance().getModContainer(MTWMod.ID).orElseThrow();
            var metadata = mtwmod.getMetadata();
            setTitleFromMetadata.invokeExact(metadata.getId(), "Cheonnyeon Story", metadata.getVersion().getFriendlyString());
        } catch (Throwable e) {
            LOGGER.info("[Mod Loading Screen] 제목 설정 실패", e);
        }
    }
}
