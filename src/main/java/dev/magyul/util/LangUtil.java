package dev.magyul.util;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.text.TextContent;
import net.minecraft.text.TranslatableTextContent;

public class LangUtil {

    public static String item2lang(Item item) {
        var id = Registries.ITEM.getId(item);
        return upper(id.getPath().replace("_", " "));
    }

    public static String block2lang(Block block) {
        var id = Registries.BLOCK.getId(block);
        return upper(id.getPath().replace("_", " "));
    }

    private static String upper(String in) {
        String[] div = in.split(" ");

        StringBuilder sb = new StringBuilder();
        for(String divA : div){
            if (!sb.isEmpty()) sb.append(" ");
            String _1 = divA.substring(0,1);
            String _1Dea = _1.toUpperCase();
            String _2 = divA.substring(1);
            String _sum = _1Dea.concat(_2);

            sb.append(_sum);
        }

        return sb.toString();
    }

    public static void addItemGroup(FabricLanguageProvider.TranslationBuilder builder, ItemGroup group, String value) {
        final TextContent content = group.getDisplayName().getContent();

        if (content instanceof TranslatableTextContent translatableTextContent) {
            builder.add(translatableTextContent.getKey(), value);
        }
    }
}
