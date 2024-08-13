package dev.magyul.util;

import dev.magyul.registers.MTWOther;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.text.TextContent;
import net.minecraft.text.TranslatableTextContent;

public class LangUtil {

    public static String block2lang(Block block) {
        var id = Registries.BLOCK.getId(block);
        String[] div = id.getPath().replace("_", " ").split(" ");

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
