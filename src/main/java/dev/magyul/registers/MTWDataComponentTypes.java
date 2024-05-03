package dev.magyul.registers;

import com.mojang.serialization.Codec;
import dev.magyul.MTWMod;
import net.minecraft.component.DataComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.UnaryOperator;

public class MTWDataComponentTypes {
    public static final DataComponentType<Boolean> IS_CARRY = register("is_carry", (builder) ->
            builder.codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL));

    public static void init() {
    }

    private static <T> DataComponentType<T> register(String id, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, new Identifier(MTWMod.ID, id), builderOperator.apply(DataComponentType.builder()).build());
    }
}
