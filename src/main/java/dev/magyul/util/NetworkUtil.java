package dev.magyul.util;

import dev.magyul.MTWMod;
import dev.magyul.network.IPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class NetworkUtil {


    /**
     * Creates a new packet type.
     * @param id the channel ID used for the packets
     * @param constructor the reader that reads the received buffer
     * @param <P> the type of the packet
     * @return the newly created type
     */
    public static <P extends IPacket> PacketType<P> create(String id, Function<PacketByteBuf, P> constructor) {
        return PacketType.create(Identifier.of(MTWMod.ID, id), constructor);
    }
}
