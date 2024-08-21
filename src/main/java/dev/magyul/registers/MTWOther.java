package dev.magyul.registers;

import dev.magyul.MTWMod;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MTWOther {
    public static final ItemGroup ITEM_GROUP_BLOCKS = register("mtwmod_group_blocks", FabricItemGroup.builder()
            .icon(MTWItems.MTW_ICON::getDefaultStack)
            .displayName(Text.translatable("itemGroup.mtw.blocks"))
            .entries((context, entries) -> {
                for (var type : MTWItems.Type.values()) {
                    var items = MTWItems.ITEMMAP.get(type);
                    for (var item : items) {
                        entries.add(item);
                    }
                }
            }).build());
    public static final ItemGroup ITEM_GROUP_ITEMS = register("mtwmod_group_items", FabricItemGroup.builder()
            .icon(MTWItems.MTW_ICON::getDefaultStack)
            .displayName(Text.translatable("itemGroup.mtw.items"))
            .entries((context, entries) -> {
                for (Item item : MTWItems.ITEMS) {
                    if (!(item instanceof BlockItem)) {
                        entries.add(item);
                    }
                }
            }).build());

    @SuppressWarnings("SameParameterValue")
    private static ItemGroup register(String name, ItemGroup group) {
        return Registry.register(Registries.ITEM_GROUP, Identifier.of(MTWMod.ID, name), group);
    }

    public static void init() {
    }
}
