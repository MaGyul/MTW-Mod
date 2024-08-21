package dev.magyul.network;

import dev.magyul.network.packets.c2s.*;
import dev.magyul.network.packets.c2s.handshake.HelloResponseC2SPacket;
import dev.magyul.network.packets.s2c.PickupReachS2CPacket;
import dev.magyul.network.packets.s2c.UpdateAllMicS2CPacket;
import dev.magyul.network.packets.s2c.handshake.HelloRequestS2CPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientCommonPacketListener;
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

        // s2c
        registerCodec(PickupReachS2CPacket.TYPE);
        registerCodec(UpdateAllMicS2CPacket.TYPE);

        HandshakeNetworking.register(HelloResponseC2SPacket.TYPE);
        HandshakeNetworking.register(HelloRequestS2CPacket.TYPE);
    }

    private static <T extends IPacket> void register(PacketType<T> type) {
        PayloadTypeRegistry.playC2S().register(type.getId(), PacketCodec.of(IPacket::write, type::read));
        ServerPlayNetworking.registerGlobalReceiver(type.getId(), (payload, context) ->
                payload.handle(new IPacket.Context(context.server(), context.player(), context.responseSender())));
    }

    private static <T extends IPacket> void registerCodec(PacketType<T> type) {
        PayloadTypeRegistry.playS2C().register(type.getId(), PacketCodec.of(IPacket::write, type::read));
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

    public static Packet<ClientCommonPacketListener> toVanillaPacket(IPacket packet) {
        return ServerPlayNetworking.createS2CPacket(packet);
    }
}
