package dev.magyul.network.packets.s2c.handshake;

import dev.magyul.MTWMod;
import dev.magyul.network.HandshakePacketType;
import dev.magyul.network.IHandshakeMessage;
import dev.magyul.network.packets.c2s.handshake.HelloResponseC2SPacket;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.PacketCallbacks;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class HelloRequestS2CPacket implements IHandshakeMessage {
    public static final HandshakePacketType<HelloRequestS2CPacket> TYPE = HandshakePacketType.create("hello_request",
            HelloRequestS2CPacket::new, HelloRequestS2CPacket::new);

    private HelloRequestS2CPacket() {}

    private HelloRequestS2CPacket(PacketByteBuf ignoredBuf) {}

    @Override
    public void write(PacketByteBuf buf) {}

    @Override
    public HandshakePacketType<?> getType() {
        return TYPE;
    }

    @Override
    public @Nullable IResponsePacket handle(ClientConnection connection, Consumer<PacketCallbacks> callbacks) {
        MTWMod.LOGGER.info("Send MTW hello request to {}", connection.getAddress());
        return new HelloResponseC2SPacket();
    }
}
