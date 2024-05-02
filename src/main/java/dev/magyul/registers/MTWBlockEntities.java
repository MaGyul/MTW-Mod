package dev.magyul.registers;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.types.Type;
import com.mojang.logging.LogUtils;
import dev.magyul.MTWMod;
import dev.magyul.blocks.entities.SignBoardEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.slf4j.Logger;

import java.util.Set;
import java.util.function.Supplier;

public class MTWBlockEntities {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final BlockEntityType<SignBoardEntity> SIGN_BOARD = register("sign_board", SignBoardEntity::new);

    public static void init() {
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(
            String id, FabricBlockEntityTypeBuilder.Factory<T> factory, Block... blocks) {
        return Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier(MTWMod.ID, id),
                FabricBlockEntityTypeBuilder.create(factory, blocks).build());
    }
}
