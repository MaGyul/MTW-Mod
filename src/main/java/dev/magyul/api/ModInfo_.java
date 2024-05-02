package dev.magyul.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

import java.util.Collection;

public class ModInfo_ {
    private final String version;
    private final String id;

    private ModInfo_(String version, String id) {
        this.version = version;
        this.id = id;
    }

    public ModInfo_(ModMetadata metadata) {
        this(metadata.getVersion().getFriendlyString(), metadata.getId());
    }

    public static ModInfo_ parse(String data) {
        var split = data.split("\0");
        return new ModInfo_(split[0], split[1]);
    }

    public static Collection<ModInfo_> parse(Collection<ModContainer> mods) {
        return mods.stream().filter(ModInfo_::filterMod).map(mod -> new ModInfo_(mod.getMetadata())).toList();
    }

    private static boolean filterMod(ModContainer mod) {
        var metadata = mod.getMetadata();
        return metadata.getEnvironment().matches(EnvType.SERVER);
    }

    @Override
    public String toString() {
        return version + "\0" + id;
    }
}
