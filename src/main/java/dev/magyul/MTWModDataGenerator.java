package dev.magyul;

import dev.magyul.data.generator.Model;
import dev.magyul.data.generator.lang.English;
import dev.magyul.data.generator.lang.Korean;
import dev.magyul.data.generator.tags.Block;
import dev.magyul.data.generator.tags.Item;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class MTWModDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        var pack = generator.createPack();

        // Languages
        pack.addProvider(English::new);
        pack.addProvider(Korean::new);

        // Tags
        pack.addProvider(Block::new);
        pack.addProvider(Item::new);

        // Model
        pack.addProvider(Model::new);
    }
}
