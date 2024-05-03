package dev.magyul.util;

import dev.magyul.MTWMod;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketDecoder;
import net.minecraft.network.codec.ValueFirstEncoder;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class NetworkUtil {
    public static <T extends CustomPayload> PacketCodec<PacketByteBuf, T> createS2C(final CustomPayload.Id<T> id, final ValueFirstEncoder<PacketByteBuf, T> encoder, final PacketDecoder<PacketByteBuf, T> decoder) {
        var codec = PacketCodec.of(encoder, decoder);
        PayloadTypeRegistry.playS2C().register(id, codec);
        return codec;
    }

    public static <T extends CustomPayload> PacketCodec<PacketByteBuf, T> createC2S(final CustomPayload.Id<T> id, final ValueFirstEncoder<PacketByteBuf, T> encoder, final PacketDecoder<PacketByteBuf, T> decoder) {
        var codec = PacketCodec.of(encoder, decoder);
        PayloadTypeRegistry.playC2S().register(id, codec);
        return codec;
    }

    public static <T extends CustomPayload> CustomPayload.Id<T> createId(String id) {
        return new CustomPayload.Id<>(new Identifier(MTWMod.ID, id));
    }
}
