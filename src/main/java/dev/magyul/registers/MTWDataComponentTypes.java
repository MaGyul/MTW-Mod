package dev.magyul.registers;

import com.mojang.serialization.Codec;
import dev.magyul.MTWMod;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.UnaryOperator;

public class MTWDataComponentTypes {
    public static final ComponentType<Boolean> IS_CARRY = register("is_carry", (builder) ->
            builder.codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL));

    public static void init() {
    }

    private static <T> ComponentType<T> register(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(MTWMod.ID, id), builderOperator.apply(ComponentType.builder()).build());
    }
}
