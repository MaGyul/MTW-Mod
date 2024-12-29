package dev.magyul.network.packets.c2s.handshake;

import dev.magyul.MTWMod;
import dev.magyul.network.HandshakePacketType;
import dev.magyul.network.IHandshakeMessage;
import dev.magyul.util.EnvironmentUtil;
import dev.magyul.util.ServerUtil;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Unique;

public class HelloResponseC2SPacket implements IHandshakeMessage.IResponsePacket {
    public static final HandshakePacketType.ResponsePacketType<HelloResponseC2SPacket> TYPE
            = HandshakePacketType.ResponsePacketType.create("hello_response", HelloResponseC2SPacket::new);

    private final String mtwmodVersion;

    public HelloResponseC2SPacket() {
        mtwmodVersion = MTWMod.VERSION;
    }

    private HelloResponseC2SPacket(PacketByteBuf buf) {
        mtwmodVersion = buf.readString();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeString(mtwmodVersion);
    }

    @Override
    public void handle(ClientConnection connection, PacketSender sender) {
        if (EnvironmentUtil.isClient()) return;
        if (!ServerUtil.allowLogins.get()) {
            var text = Text.literal("Server is still starting! Please wait before reconnecting.");
            MTWMod.LOGGER.info("Disconnecting Player (server is still starting): {}", text.getString());
            rejectConnection(connection, text);
        } else {
            if (!mtwmodVersion.equals(MTWMod.VERSION)) {
                rejectConnection(connection, Text.translatable("disconnect.versionNotMatched", MTWMod.VERSION, mtwmodVersion));
            }
        }
    }

    @Override
    public HandshakePacketType.ResponsePacketType<?> getType() {
        return TYPE;
    }

    @Unique
    private void rejectConnection(ClientConnection connection, Text message) {
        connection.send(new LoginDisconnectS2CPacket(message));
        connection.disconnect(message);
    }
}
