package dev.magyul.registers;

import dev.magyul.MTWMod;
import dev.magyul.blocks.entities.FoodTableBlockEntity;
import dev.magyul.blocks.entities.StandardBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static net.minecraft.block.entity.BlockEntityType.Builder.create;

public class MTWBlockEntityType {
    public static final BlockEntityType<StandardBlockEntity> STANDARD_BLOCK_ENTITY = register(
            "standard_block_entity", create(StandardBlockEntity::new, MTWBlocks.STANDARD)
    );
    public static final BlockEntityType<FoodTableBlockEntity> FOOD_TABLE_BLOCK_ENTITY = register(
            "food_table_block_entity", create(FoodTableBlockEntity::new, MTWBlocks.FOOD_TABLE)
    );

    private static <T extends BlockEntity> BlockEntityType<T> register(String id, BlockEntityType.Builder<T> builder) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MTWMod.ID, id), builder.build());
    }

    public static void init() {
    }
}
