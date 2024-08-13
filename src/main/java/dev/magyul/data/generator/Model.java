package dev.magyul.data.generator;

import dev.magyul.MTWMod;
import dev.magyul.registers.MTWItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class Model extends FabricModelProvider {
    public Model(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {
    }

    @Override
    public void generateItemModels(ItemModelGenerator generator) {
        // Items
        generator.register(MTWItems.MTW_ICON, Models.GENERATED);
        generator.register(MTWItems.MTW_REGION_VIEWER, Models.GENERATED);

        // Blocks
        for (Item item : MTWItems.ITEMS) {
            if (item instanceof BlockItem) {
                var id = Registries.ITEM.getId(item);
                generator.register(item, item(id.getPath()));
            }
        }
    }

    //{
    //  "parent": "mtwmod:block/{parent}/inventory"
    //}

    private static net.minecraft.data.client.Model item(String parent) {
        Identifier identifier = new Identifier(MTWMod.ID, "block/" + parent + "/inventory");
        return new net.minecraft.data.client.Model(Optional.of(identifier), Optional.empty());
    }
}
