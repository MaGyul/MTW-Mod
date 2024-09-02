package dev.magyul.cmdkeybind;

import net.kyrptonaught.cmdkeybind.CmdKeybindMod;
import net.kyrptonaught.cmdkeybind.MacroTypes.BaseMacro;
import net.kyrptonaught.cmdkeybind.config.ConfigOptions;
import net.kyrptonaught.kyrptconfig.config.screen.ConfigSection;
import net.kyrptonaught.kyrptconfig.config.screen.items.*;
import net.kyrptonaught.kyrptconfig.config.screen.items.number.IntegerItem;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

import java.util.List;

public class ConfigMacroItem extends SubItem<ConfigOptions.ConfigMacro> {
    @SuppressWarnings("unchecked")
    public ConfigMacroItem(ConfigSection configSection, ConfigOptions.ConfigMacro macro) {
        super(Text.literal(macro.command));
        this.setToolTip(Text.literal(macro.keyName));
        CommandTextItem command = (CommandTextItem) this.addConfigItem(new CommandTextItem(Text.translatable("key.cmdkeybind.config.macro.command"), macro.command, "/").setMaxLength(1024)
                .setSaveConsumer((cmd) -> macro.command = cmd));
        KeybindItem keyItem = (KeybindItem)this.addConfigItem((new KeybindItem(Text.translatable("key.cmdkeybind.config.macro.key"), macro.keyName, InputUtil.Type.KEYSYM.createFromCode(320).getTranslationKey()))
                .setSaveConsumer((key) -> macro.keyName = key));
        this.addConfigItem((new KeybindItem(Text.translatable("key.cmdkeybind.config.macro.keymod"), macro.keyModName, InputUtil.UNKNOWN_KEY.getTranslationKey()))
                .setSaveConsumer((key) -> macro.keyModName = key));
        EnumItem<BaseMacro.MacroType> macroType = (EnumItem<BaseMacro.MacroType>) this.addConfigItem((new EnumItem<>(Text.translatable("key.cmdkeybind.config.macrotype"), BaseMacro.MacroType.values(), macro.macroType, BaseMacro.MacroType.SingleUse))
                .setSaveConsumer((val) -> macro.macroType = val));
        IntegerItem delayItem = (IntegerItem)this.addConfigItem((new IntegerItem(Text.translatable("key.cmdkeybind.config.delay"), macro.delay, 0))
                .setSaveConsumer((val) -> macro.delay = val));
        IntegerItem repetitionsItem = (IntegerItem)this.addConfigItem((new IntegerItem(Text.translatable("key.cmdkeybind.config.repetitions"), macro.repetitions, 1))
                .setSaveConsumer((val) -> macro.repetitions = val));
        this.addConfigItem((new ButtonItem(Text.translatable("key.cmdkeybind.config.remove"))).setClickEvent(() -> {
            CmdKeybindMod.getConfig().macros.remove(macro);
            configSection.configs.remove(this);
        }));
        command.setValueUpdatedEvent((value) -> this.setTitleText(Text.literal(value)));
        keyItem.setValueUpdatedEvent((value) -> this.setToolTip(Text.literal(value)));
        macroType.setValueUpdatedEvent((value) -> {
            delayItem.setHidden(!value.isDelayApplicable());
            repetitionsItem.setHidden(!value.isRepetitionsApplicable());
        });
        delayItem.setHidden(!macro.macroType.isDelayApplicable());
        repetitionsItem.setHidden(!macro.macroType.isRepetitionsApplicable());
    }

    public List<ConfigItem<?>> getConfigs() {
        return configs;
    }
}
