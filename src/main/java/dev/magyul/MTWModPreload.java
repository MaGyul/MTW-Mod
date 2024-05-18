package dev.magyul;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.LanguageAdapter;
import net.fabricmc.loader.api.LanguageAdapterException;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class MTWModPreload implements LanguageAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(MTWModPreload.class);

    @Override
    @SuppressWarnings("unchecked")
    public <T> T create(ModContainer mod, String value, Class<T> type) throws LanguageAdapterException {
        if (type != PreLaunchEntrypoint.class) {
            throw new LanguageAdapterException("Fake entrypoint only supported on PreLaunchEntrypoint");
        }
        return (T)(PreLaunchEntrypoint)() -> {};
    }

    static {
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
            setTitleFromMetadata.invokeExact(metadata.getId(), "Make The World", metadata.getVersion().getFriendlyString());
        } catch (Throwable e) {
            LOGGER.info("[Mod Loading Screen] 제목 설정 실패", e);
        }
    }
}
