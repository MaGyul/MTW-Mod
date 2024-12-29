package dev.magyul.network;

import dev.magyul.network.packets.c2s.*;
import dev.magyul.network.packets.c2s.handshake.HelloResponseC2SPacket;
import dev.magyul.network.packets.s2c.handshake.HelloRequestS2CPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.world.World;

public class NetworkHandler {

    public static void init() {
        HandshakeNetworking.init();

        // c2s
        register(AttackAirC2SPacket.TYPE);
        register(ErrorBlockUpdateC2SPacket.TYPE);
        register(KeyInputC2SPacket.TYPE);
        register(PickupItemC2SPacket.TYPE);
        register(UseAirC2SPacket.TYPE);

        HandshakeNetworking.register(HelloResponseC2SPacket.TYPE);
        HandshakeNetworking.register(HelloRequestS2CPacket.TYPE);
    }

    private static <T extends IPacket> void register(PacketType<T> type) {
        ServerPlayNetworking.registerGlobalReceiver(type, (packet, player, responseSender) ->
                packet.handle(new IPacket.Context(player, player, responseSender)));
    }

    @Environment(EnvType.CLIENT)
    public static void sendToServer(IPacket message) {
        NetworkClientInitializer.sendToServer(message);
    }

    public static void sendToClientPlayer(IPacket message, PlayerEntity player) {
        ServerPlayNetworking.send((ServerPlayerEntity) player, message);
    }

    /**
     * Sent to all players listening to this entity
     */
    public static void sendToTrackingEntityAndSelf(Entity centerEntity, IPacket message) {
        ((ServerChunkManager)centerEntity.getEntityWorld().getChunkManager())
                .sendToNearbyPlayers(centerEntity, toVanillaPacket(message));
    }

    public static void sendToAllPlayers(MinecraftServer server, IPacket message) {
        server.getPlayerManager().sendToAll(toVanillaPacket(message));
    }

    public static void sendToTrackingEntity(IPacket message, final Entity centerEntity) {
        ((ServerChunkManager)centerEntity.getEntityWorld().getChunkManager())
                .sendToOtherNearbyPlayers(centerEntity, toVanillaPacket(message));
    }

    public static void sendToDimension(IPacket message, final Entity centerEntity) {
        RegistryKey<World> dimension = centerEntity.getWorld().getRegistryKey();
        var server = centerEntity.getServer();
        if (server != null) {
            server.getPlayerManager().sendToDimension(toVanillaPacket(message), dimension);
        }
    }

    public static Packet<ClientPlayPacketListener> toVanillaPacket(IPacket packet) {
        PacketByteBuf buf = PacketByteBufs.create();
        packet.write(buf);
        return ServerPlayNetworking.createS2CPacket(packet.getType().getId(), buf);
    }
}
