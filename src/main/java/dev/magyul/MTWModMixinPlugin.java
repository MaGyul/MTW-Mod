package dev.magyul;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MTWModMixinPlugin implements IMixinConfigPlugin {

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        var fabricLoader = FabricLoader.getInstance();
        return switch (mixinClassName) {
            case "dev.magyul.mixin.client.cocoainput.MultilineTextFieldWidgetMixin" ->
                    fabricLoader.isModLoaded("command-block-ide");
            case "dev.magyul.mixin.client.voicechat.AudioChannelMixin", "dev.magyul.mixin.voicechat.NetworkMessageMixin", "dev.magyul.mixin.voicechat.ServerMixin" ->
                    fabricLoader.isModLoaded("voicechat");
            case "dev.magyul.mixin.client.cmdkeybind.ConfigSectionMixin" ->
                    fabricLoader.isModLoaded("kyrptconfig");
            case "dev.magyul.mixin.client.cmdkeybind.MacroScreenFactoryMixin" ->
                    fabricLoader.isModLoaded("cmdkeybind");
            case "dev.magyul.mixin.pehkui.ReflectionUtilsMixin" ->
                    fabricLoader.isDevelopmentEnvironment();
            default -> true;
        };
    }

    @Override
    public void onLoad(String mixinPackage) {
        MixinExtrasBootstrap.init();
//        MTWMod.LOGGER.info("Loading Mixin Plugin for " + Authorization.getUserAgent());
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
