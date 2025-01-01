package dev.magyul.network;

import dev.magyul.MTWMod;
import dev.magyul.network.packets.s2c.PickupReachS2CPacket;
import dev.magyul.network.packets.s2c.UpdateAllMicS2CPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketType;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class NetworkClientInitializer {
    private static Consumer<PacketType<? extends IPacket>> registerConsumer = null;
    private static Consumer<HandshakePacketType<? extends IHandshakeMessage>> registerHandshakeConsumer = null;
    private static PlayPayloadHandler callerHandler = null;
    private static Consumer<IPacket> sendToServerConsumer = null;

    public static void init() {
        register(PickupReachS2CPacket.TYPE);
        register(UpdateAllMicS2CPacket.TYPE);

        for (HandshakePacketType<? extends IHandshakeMessage> type : HandshakeNetworking.HANDSHAKE_PACKETS) {
            MTWMod.LOGGER.info("Registering client handshake packet type {}", type.getId());
            registerHandshake(type);
        }
    }

    private static <T extends IPacket> void register(PacketType<T> type) {
        if (registerConsumer != null) {
            registerConsumer.accept(type);
        } else {
            MTWMod.LOGGER.error("Registering packet type {} failed", type);
        }
    }

    @Environment(EnvType.CLIENT)
    private static <T extends IHandshakeMessage> void registerHandshake(HandshakePacketType<T> type) {
        if (registerHandshakeConsumer != null) {
            registerHandshakeConsumer.accept(type);
        } else {
            MTWMod.LOGGER.error("Registering handshake packet type {} failed", type);
        }
    }

    @Environment(EnvType.CLIENT)
    static void sendToServer(IPacket message) {
        if (sendToServerConsumer != null) {
            sendToServerConsumer.accept(message);
        } else {
            MTWMod.LOGGER.error("Sending packet {} failed", message);
        }
    }

    public static <T extends IPacket> void callClient(T packet, IPacket.Context context) {
        if (callerHandler != null) {
            callerHandler.handle(packet, context);
        } else {
            MTWMod.LOGGER.error("Calling packet {} failed", packet);
        }
    }

    protected static void initCallerHandler(PlayPayloadHandler consumer) {
        callerHandler = consumer;
    }

    protected static void initSendToServerConsumer(Consumer<IPacket> consumer) {
        if (sendToServerConsumer != null) {
            throw new IllegalArgumentException("sendToServerConsumer is already registered.");
        }
        sendToServerConsumer = consumer;
    }

    @SuppressWarnings("unchecked")
    protected static <T extends IPacket> void initRegisterConsumer(Consumer<PacketType<T>> consumer) {
        if (registerConsumer != null) {
            throw new IllegalArgumentException("registerConsumer is already registered.");
        }
        registerConsumer = type -> consumer.accept((PacketType<T>) type);
    }

    @SuppressWarnings("unchecked")
    protected static <T extends IHandshakeMessage> void initRegisterHandshakeConsumer(Consumer<HandshakePacketType<T>> consumer) {
        if (registerHandshakeConsumer != null) {
            throw new IllegalArgumentException("registerHandshakeConsumer is already registered.");
        }
        registerHandshakeConsumer = type -> consumer.accept((HandshakePacketType<T>) type);
    }

    public interface PlayPayloadHandler {
        void handle(IPacket payload, IPacket.Context context);
    }
}
