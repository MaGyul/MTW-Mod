package dev.magyul.network;

import dev.magyul.MTWMod;
import dev.magyul.mixin.accessors.ServerLoginNetworkHandlerAccessor;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandshakeNetworking {
    protected static final List<HandshakePacketType<? extends IHandshakeMessage>> HANDSHAKE_PACKETS = new ArrayList<>();
    private static final Map<Identifier, HandshakePacketType.ResponsePacketType<? extends IHandshakeMessage.IResponsePacket>> RES_PACKETS = new HashMap<>();

    public static void init() {
        ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) -> {
            for (HandshakePacketType<? extends IHandshakeMessage> packetType : HANDSHAKE_PACKETS) {
                try {
                    IHandshakeMessage packet = packetType.create();
                    packet.sendPacket(sender);
                } catch (Exception e) {
                    MTWMod.LOGGER.error("{} Handshake packet processing error", packetType.getId().toString(), e);
                }
            }
        });
    }

    public static <T extends IHandshakeMessage.IResponsePacket> void register(@NotNull HandshakePacketType.ResponsePacketType<T> type) {
        RES_PACKETS.put(type.getId(), type);
    }

    public static <T extends IHandshakeMessage> void register(@NotNull HandshakePacketType<T> type) {
        HANDSHAKE_PACKETS.add(type);

        ServerLoginNetworking.registerGlobalReceiver(type.getId(), (server, handler, understood, buf, synchronizer, responseSender) -> {
            if (!understood) {
                ClientConnection connection = ((ServerLoginNetworkHandlerAccessor) handler).getConnection();
                Text message = Text.literal("This server can be accessed through a launcher created by Make The World.");
                connection.send(new LoginDisconnectS2CPacket(message));
                connection.disconnect(message);
                return; // The client is likely a vanilla client.
            }
            try {
                if (buf.readableBytes() > 0) {
                    Identifier packetId = buf.readIdentifier();
                    HandshakePacketType.ResponsePacketType<? extends IHandshakeMessage.IResponsePacket> ackPacketType = RES_PACKETS.get(packetId);
                    if (ackPacketType == null) {
                        MTWMod.LOGGER.error("{} Is the handshake response packet registered?", packetId.toString());
                        return;
                    }
                    IHandshakeMessage.IResponsePacket packet = ackPacketType.read(buf);

                    ClientConnection connection = ((ServerLoginNetworkHandlerAccessor) handler).getConnection();
                    packet.handle(connection, responseSender);
                }
            } catch (Exception e) {
                MTWMod.LOGGER.error("Handshake response packet processing error in {}", type.getId().toString(), e);
            }
        });
    }
}
