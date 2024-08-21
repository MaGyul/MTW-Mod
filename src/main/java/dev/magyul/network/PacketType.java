package dev.magyul.network;

import dev.magyul.MTWMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class PacketType<T extends IPacket> {
    private final CustomPayload.Id<T> id;
    private final Function<RegistryByteBuf, T> constructor;

    private PacketType(CustomPayload.Id<T> id, Function<RegistryByteBuf, T> constructor) {
        this.id = id;
        this.constructor = constructor;
    }

    /**
     * Creates a new packet type.
     * @param id the channel ID used for the packets
     * @param constructor the reader that reads the received buffer
     * @param <P> the type of the packet
     * @return the newly created type
     */
    public static <P extends IPacket> PacketType<P> create(String id, Function<RegistryByteBuf, P> constructor) {
        return new PacketType<>(new CustomPayload.Id<>(Identifier.of(MTWMod.ID, id)), constructor);
    }

    /**
     * Creates a new packet type.
     * @param id the channel ID used for the packets
     * @param constructor the reader that reads the received buffer
     * @param <P> the type of the packet
     * @return the newly created type
     */
    public static <P extends IPacket> PacketType<P> create(Identifier id, Function<RegistryByteBuf, P> constructor) {
        return new PacketType<>(new CustomPayload.Id<>(id), constructor);
    }

    /**
     * Returns the identifier of the channel used to send the packet.
     * @return the identifier of the associated channel.
     */
    public CustomPayload.Id<T> getId() {
        return id;
    }

    /**
     * Reads the packet from the buffer.
     * @param buf the buffer
     * @return the packet
     */
    public T read(RegistryByteBuf buf) {
        try {
            return this.constructor.apply(buf);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error while handling packet \"%s\": %s".formatted(this.id, e.getMessage()), e);
        }
    }
}
