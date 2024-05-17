package dev.magyul.mixin.client.cmdkeybind;

import dev.magyul.cmdkeybind.CommandTextItem;
import dev.magyul.cmdkeybind.ConfigMacroItem;
import net.kyrptonaught.kyrptconfig.config.screen.ConfigSection;
import net.kyrptonaught.kyrptconfig.config.screen.items.ConfigItem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ConfigSection.class)
public abstract class ConfigSectionMixin extends Screen {

    @Shadow(remap = false) public List<ConfigItem<?>> configs;

    protected ConfigSectionMixin(Text title) {
        super(title);
    }

    @Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
    private void mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount, CallbackInfoReturnable<Boolean> cir) {
        for (ConfigItem<?> item : configs) {
            if (item instanceof ConfigMacroItem macroItem) {
                var configs = macroItem.getConfigs();
                for (ConfigItem<?> config : configs) {
                    if (config instanceof CommandTextItem cti) {
                        if (cti.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
                            cir.setReturnValue(true);
                            return;
                        }
                    }
                }
            }
        }
    }
}
