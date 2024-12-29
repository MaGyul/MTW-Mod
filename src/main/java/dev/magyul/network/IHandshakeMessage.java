package dev.magyul.network;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface IHandshakeMessage {

    void write(PacketByteBuf buf);

    HandshakePacketType<?> getType();

    @Nullable
    IResponsePacket handle(ClientConnection connection, Consumer<GenericFutureListener<? extends Future<? super Void>>> callbacks);

    default void sendPacket(PacketSender sender) {
        PacketByteBuf buf = PacketByteBufs.create();
        write(buf);
        sender.sendPacket(getType().getId(), buf);
    }

    interface IResponsePacket {
        void write(PacketByteBuf buf);

        void handle(ClientConnection connection, PacketSender sender);

        HandshakePacketType.ResponsePacketType<?> getType();

        default Identifier getId() {
            return getType().getId();
        }
    }
}
