package dev.magyul.registers;

import dev.magyul.MTWMod;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class Other {
    public static final ItemGroup ITEM_GROUP = register("mtwmod_group", FabricItemGroup.builder()
            .icon(() -> new ItemStack(MTWItems.MTW_ICON))
            .displayName(Text.translatable("itemGroup.mtw"))
            .entries((context, entries) -> {
                for (var type : MTWItems.Type.values()) {
                    var items = MTWItems.ITEMMAP.get(type);
                    for (var item : items) {
                        entries.add(item);
                    }
                }
            }).build());

    @SuppressWarnings("SameParameterValue")
    private static ItemGroup register(String name, ItemGroup group) {
        return Registry.register(Registries.ITEM_GROUP, new Identifier(MTWMod.ID, name), group);
    }

    public static void init() {
    }
}
