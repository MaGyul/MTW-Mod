package dev.magyul.mixin.network;

import dev.magyul.MTWMod;
import dev.magyul.api.MTWLoginHello;
import dev.magyul.util.ServerUtil;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.handshake.ConnectionIntent;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLoginNetworkHandler.class)
public class ServerHandshakeNetworkHandlerMixin {
    //Connection refused: no further information
    @Shadow @Final ClientConnection connection;

    @Inject(method = "onHello", at = @At("HEAD"), cancellable = true)
    private void onHello(LoginHelloC2SPacket packet, CallbackInfo cb) {
        if (!ServerUtil.allowLogins.get()) {
            var text = Text.literal("Server is still starting! Please wait before reconnecting.");
            MTWMod.LOGGER.info("Disconnecting Player (server is still starting): {}", text.getString());
            connection.send(new LoginDisconnectS2CPacket(text));
            connection.disconnect(text);
            cb.cancel();
        } else {
            var version = ((MTWLoginHello) (Object) packet).fabric$mtwmodVersion();
            if (version == null) {
                rejectConnection(Text.literal("This server can be accessed through a launcher created by Make The World."));
                cb.cancel();
                return;
            }

            if (!version.equals(MTWMod.VERSION)) {
                rejectConnection(Text.translatable("disconnect.versionNotMatched", MTWMod.VERSION, version));
                cb.cancel();
            }
        }
    }

    @Unique
    private void rejectConnection(Text message) {
//        connection.setS2CPacketHandler(ConnectionIntent.LOGIN);
        connection.send(new LoginDisconnectS2CPacket(message));
        connection.disconnect(message);
    }
}
