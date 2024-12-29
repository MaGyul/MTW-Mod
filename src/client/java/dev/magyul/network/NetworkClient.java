package dev.magyul.network;

import dev.magyul.mixin.client.accessor.ClientLoginNetworkHandlerAccessor;
import dev.magyul.network.packets.s2c.PickupReachS2CPacket;
import dev.magyul.network.packets.s2c.UpdateAllMicS2CPacket;
import dev.magyul.util.ClientUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;

import java.util.concurrent.CompletableFuture;

public class NetworkClient {

    public static void init() {
        NetworkClientInitializer.initRegisterConsumer(type -> {
            ClientPlayNetworking.registerGlobalReceiver(type, (packet, player, responseSender) ->
                    packet.handle(new IPacket.Context(MinecraftClient.getInstance(), player, responseSender)));
        });
        NetworkClientInitializer.initRegisterHandshakeConsumer(type ->
                ClientLoginNetworking.registerGlobalReceiver(type.getId(), (client, handler, buf, callbacks) -> {
                    IHandshakeMessage packet = type.read(buf);
                    ClientConnection connection = ((ClientLoginNetworkHandlerAccessor) handler).getConnection();
                    IHandshakeMessage.IResponsePacket responsePacket = packet.handle(connection, callbacks);
                    PacketByteBuf response = PacketByteBufs.create();
                    if (responsePacket != null) {
                        response.writeIdentifier(responsePacket.getId());
                        responsePacket.write(response);
                    }
                    return CompletableFuture.completedFuture(response);
                }));
        NetworkClientInitializer.initCallerHandler(NetworkClient::call);
        NetworkClientInitializer.initSendToServerConsumer(ClientPlayNetworking::send);
        NetworkClientInitializer.init();
    }

    private static <T extends IPacket> void call(T packetClass, IPacket.Context context) {
        if (packetClass instanceof UpdateAllMicS2CPacket packet) {
            ClientUtil.setAllMic(packet.value());
        } else if (packetClass instanceof PickupReachS2CPacket packet) {
            ClientUtil.pickupReach = packet.reach();
        }
    }
}
