package dev.magyul.mixin.client.cmdkeybind;

import dev.magyul.cmdkeybind.ConfigMacroItem;
import dev.magyul.util.ClientUtil;
import net.kyrptonaught.cmdkeybind.CmdKeybindMod;
import net.kyrptonaught.cmdkeybind.config.ConfigOptions;
import net.kyrptonaught.cmdkeybind.config.MacroScreenFactory;
import net.kyrptonaught.kyrptconfig.config.screen.ConfigScreen;
import net.kyrptonaught.kyrptconfig.config.screen.ConfigSection;
import net.kyrptonaught.kyrptconfig.config.screen.items.BooleanItem;
import net.kyrptonaught.kyrptconfig.config.screen.items.ButtonItem;
import net.kyrptonaught.kyrptconfig.config.screen.items.KeybindItem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MacroScreenFactory.class)
public class MacroScreenFactoryMixin {
    @Inject(method = "buildScreen", at = @At("HEAD"), cancellable = true)
    private static void buildScreen(Screen screen, CallbackInfoReturnable<Screen> cir) {
        ConfigOptions options = CmdKeybindMod.getConfig();
        if (!ClientUtil.checkServerDev()) {
            options.enabled = false;
            CmdKeybindMod.config.save();
            CmdKeybindMod.buildMacros();
            cir.setReturnValue(screen);
            return;
        }
        ConfigScreen configScreen = new ConfigScreen(screen, Text.translatable("key.cmdkeybind.config.title"));
        configScreen.setSavingEvent(() -> {
            CmdKeybindMod.config.save();
            CmdKeybindMod.buildMacros();
        });
        ConfigSection mainSection = new ConfigSection(configScreen, Text.translatable("key.cmdkeybind.config.category.main"));
        mainSection.addConfigItem(new BooleanItem(Text.translatable("key.cmdkeybind.config.enabled"), options.enabled, true)
                .setSaveConsumer((val) -> options.enabled = val));
        mainSection.addConfigItem(new KeybindItem(Text.translatable("key.cmdkeybind.config.openmacrokeybind"), options.openMacroScreenKeybind.rawKey, "key.keyboard.unknown")
                .setSaveConsumer((val) -> options.openMacroScreenKeybind.setRaw(val)));

        for(int i = 0; i < options.macros.size(); ++i) {
            mainSection.addConfigItem(new ConfigMacroItem(mainSection, options.macros.get(i)));
        }

        mainSection.addConfigItem((new ButtonItem(Text.translatable("key.cmdkeybind.config.add"))).setClickEvent(() -> {
            CmdKeybindMod.addEmptyMacro();
            mainSection.insertConfigItem(new ConfigMacroItem(mainSection, options.macros.get(options.macros.size() - 1)), mainSection.configs.size() - 1);
        }));
        cir.setReturnValue(configScreen);
    }
}
