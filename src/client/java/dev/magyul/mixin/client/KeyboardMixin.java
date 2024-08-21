package dev.magyul.mixin.client;

import dev.magyul.network.packets.c2s.KeyInputC2SPacket;
import dev.magyul.util.ClientUtil;
import dev.magyul.util.ConnectServer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.DirectConnectScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onKey", at = @At("HEAD"))
    private void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo cb) {
        if (window == client.getWindow().getHandle()) {
            if (action != 1 && action != 2) {
                if (action == 0) {
                    if (client.getNetworkHandler() != null) {
                        ClientPlayNetworking.send(new KeyInputC2SPacket(key, true));
                    }
                }
            } else {
                if (client.getNetworkHandler() != null) {
                    ClientPlayNetworking.send(new KeyInputC2SPacket(key, false));
                }
                onKeyScreen(client.currentScreen, key);
            }
        }
    }

    @Unique
    private boolean single = false;

    @Unique
    private void onKeyScreen(Screen screen, int keyCode) {
        if (screen instanceof TitleScreen) {
            if (keyCode == GLFW.GLFW_KEY_S) {
                if (single && ClientUtil.checkDev()) {
                    if (ClientUtil.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
                        ClientUtil.setTimeout(this::otherServer, 100);
                    } else {
                        ClientUtil.setTimeout(() -> client.setScreen(new SelectWorldScreen(screen)), 100);
                    }
                }
                single = false;
            }
            if (ClientUtil.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL)
                    && ClientUtil.isKeyDown(GLFW.GLFW_KEY_LEFT_ALT)
                    && ClientUtil.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
                if (keyCode == GLFW.GLFW_KEY_I) {
                    single = true;
                }
            }
        }
    }

    @Unique
    private void otherServer() {
        ServerInfo info = new ServerInfo("Minecraft Server", "", ServerInfo.ServerType.OTHER);
        client.setScreen(new DirectConnectScreen(new TitleScreen(), b -> {
            if (b) {
                ClientUtil.cs = ConnectServer.startConnecting(client, ServerAddress.parse(info.address), info);
            } else {
                client.setScreen(new TitleScreen());
            }
        }, info));
    }
}
