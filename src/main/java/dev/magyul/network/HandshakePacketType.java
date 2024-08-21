package dev.magyul.network;

import dev.magyul.MTWMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.function.Function;
import java.util.function.Supplier;

public class HandshakePacketType<T extends IHandshakeMessage> {
    private final Identifier id;
    private final Supplier<T> getter;
    private final Function<PacketByteBuf, T> constructor;

    private HandshakePacketType(Identifier id, Supplier<T> getter, Function<PacketByteBuf, T> constructor) {
        this.id = id;
        this.getter = getter;
        this.constructor = constructor;
    }

    /**
     * Creates a new packet type.
     * @param id the channel ID used for the packets
     * @param constructor the reader that reads the received buffer
     * @param <P> the type of the packet
     * @return the newly created type
     */
    public static <P extends IHandshakeMessage> HandshakePacketType<P> create(String id, Supplier<P> getter, Function<PacketByteBuf, P> constructor) {
        return new HandshakePacketType<>(Identifier.of(MTWMod.ID, id), getter, constructor);
    }

    /**
     * Creates a new packet type.
     * @param id the channel ID used for the packets
     * @param constructor the reader that reads the received buffer
     * @param <P> the type of the packet
     * @return the newly created type
     */
    public static <P extends IHandshakeMessage> HandshakePacketType<P> create(Identifier id, Supplier<P> getter, Function<PacketByteBuf, P> constructor) {
        return new HandshakePacketType<>(id, getter, constructor);
    }

    /**
     * Returns the identifier of the channel used to send the packet.
     * @return the identifier of the associated channel.
     */
    public Identifier getId() {
        return id;
    }

    /**
     * Reads the packet from the buffer.
     * @param buf the buffer
     * @return the packet
     */
    public T read(PacketByteBuf buf) {
        try {
            return this.constructor.apply(buf);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error while handling packet \"%s\": %s".formatted(this.id, e.getMessage()), e);
        }
    }

    public T create() {
        try {
            return this.getter.get();
        } catch (RuntimeException e) {
            throw new RuntimeException("Error while handling packet \"%s\": %s".formatted(this.id, e.getMessage()), e);
        }
    }

    public static class ResponsePacketType<E extends IHandshakeMessage.IResponsePacket> {
        private final Identifier id;
        private final Function<PacketByteBuf, E> constructor;

        private ResponsePacketType(Identifier id, Function<PacketByteBuf, E> constructor) {
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
        public static <P extends IHandshakeMessage.IResponsePacket> ResponsePacketType<P> create(String id, Function<PacketByteBuf, P> constructor) {
            return new ResponsePacketType<>(Identifier.of(MTWMod.ID, id), constructor);
        }

        /**
         * Creates a new packet type.
         * @param id the channel ID used for the packets
         * @param constructor the reader that reads the received buffer
         * @param <P> the type of the packet
         * @return the newly created type
         */
        public static <P extends IHandshakeMessage.IResponsePacket> ResponsePacketType<P> create(Identifier id, Function<PacketByteBuf, P> constructor) {
            return new ResponsePacketType<>(id, constructor);
        }

        /**
         * Returns the identifier of the channel used to send the packet.
         * @return the identifier of the associated channel.
         */
        public Identifier getId() {
            return id;
        }

        /**
         * Reads the packet from the buffer.
         * @param buf the buffer
         * @return the packet
         */
        public E read(PacketByteBuf buf) {
            try {
                return this.constructor.apply(buf);
            } catch (RuntimeException e) {
                throw new RuntimeException("Error while handling packet \"%s\": %s".formatted(this.id, e.getMessage()), e);
            }
        }
    }
}
