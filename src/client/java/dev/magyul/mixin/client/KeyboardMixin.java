package dev.magyul.mixin.client;

import dev.magyul.MTWModClient;
import dev.magyul.network.KeyInputC2SPacket;
import dev.magyul.screen.DisconnectedPopupScreen;
import dev.magyul.util.ClientUtil;
import dev.magyul.util.ConnectServer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.navigation.GuiNavigationType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.DirectConnectScreen;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.client.gui.screen.option.SimpleOptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.NarratorMode;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
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
//                if (key == GLFW.GLFW_KEY_F10) {
//                    MTWModClient.instance.setOverlayScreen(new DisconnectedPopupScreen(
//                            Text.translatable("disconnect.lost"),
//                            Text.literal("org.spongepowered.asm.mixin.injection.throwables.InjectionError: Critical injection failure: Callback method render(FJZLorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;Ljava/lang/Object;)V in mtwmod.client.mixins.json:GameRendererMixin from mod mtwmod failed injection check, (0/1) succeeded. Scanned 1 target(s). No refMap loaded.")
//                    ));
//                }
            }
            if (MTWModClient.checkOverlayUsed()) {
                var screen = MTWModClient.instance.overlayScreen;
                switch (key) {
                    case GLFW.GLFW_KEY_TAB:
                        client.setNavigationType(GuiNavigationType.KEYBOARD_TAB);
                    case GLFW.GLFW_KEY_BACKSPACE:
                    case GLFW.GLFW_KEY_INSERT:
                    case GLFW.GLFW_KEY_DELETE:
                    default:
                        break;
                    case GLFW.GLFW_KEY_RIGHT:
                    case GLFW.GLFW_KEY_LEFT:
                    case GLFW.GLFW_KEY_DOWN:
                    case GLFW.GLFW_KEY_UP:
                        client.setNavigationType(GuiNavigationType.KEYBOARD_ARROW);
                }

                if (action == 1 && (!(this.client.currentScreen instanceof KeybindsScreen) || ((KeybindsScreen)this.client.currentScreen).lastKeyCodeUpdateTime <= Util.getMeasuringTimeMs() - 20L)) {
                    if (this.client.options.fullscreenKey.matchesKey(key, scancode)) {
                        this.client.getWindow().toggleFullscreen();
                        this.client.options.getFullscreen().setValue(this.client.getWindow().isFullscreen());
                        return;
                    }

                    if (this.client.options.screenshotKey.matchesKey(key, scancode)) {
                        ScreenshotRecorder.saveScreenshot(this.client.runDirectory, this.client.getFramebuffer(), (message) ->
                                this.client.execute(() -> this.client.inGameHud.getChatHud().addMessage(message)));
                        return;
                    }
                }

                boolean bl2;
                boolean bl3;
                if (client.getNarratorManager().isActive() && client.options.getNarratorHotkey().getValue()) {
                    label156: {
                        var element = screen.getFocused();
                        if (element instanceof TextFieldWidget widget) {
                            if (widget.isActive()) {
                                bl2 = false;
                                break label156;
                            }
                        }

                        bl2 = true;
                    }

                    if (action != 0 && key == GLFW.GLFW_KEY_B && Screen.hasControlDown() && bl2) {
                        bl3 = client.options.getNarrator().getValue() == NarratorMode.OFF;
                        this.client.options.getNarrator().setValue(NarratorMode.byId(this.client.options.getNarrator().getValue().getId() + 1));
                        this.client.options.write();

                        if (bl3) {
                            screen.applyNarratorModeChangeDelay();
                        }
                    }
                }

                if (screen != null) {
                    AtomicBoolean cancelled = new AtomicBoolean(false);
                    Screen.wrapScreenError(() -> {
                        if (action != 1 && action != 2) {
                            if (action == 0) {
                                cancelled.set(screen.keyReleased(key, scancode, modifiers));
                            }
                        } else {
                            screen.applyKeyPressNarratorDelay();
                            cancelled.set(screen.keyPressed(key, scancode, modifiers));
                        }
                    }, "keyPressed event handler", screen.getClass().getCanonicalName());
                    if (cancelled.get()) {
                        cb.cancel();
                    }
                }
            }
        }
    }

    @ModifyVariable(method = "onChar", at = @At("STORE"), ordinal = 0)
    private Element modifyOnChat(Element element) {
        if (MTWModClient.checkOverlayUsed()) {
            return MTWModClient.instance.overlayScreen;
        }
        return element;
    }

    /*
    @Inject(method = "onChar", at = @At("HEAD"))
    private void onChar(long window, int codePoint, int modifiers, CallbackInfo ci) {
        if (window == client.getWindow().getHandle() && MTWModClient.instance != null) {
            var element = MTWModClient.instance.overlayScreen;
            if (element != null && client.getOverlay() == null) {
                if (Character.charCount(codePoint) == 1) {
                    Screen.wrapScreenError(() ->
                            element.charTyped((char) codePoint, modifiers),
                            "charTyped event handler", element.getClass().getCanonicalName());
                } else {
                    char[] chars = Character.toChars(codePoint);

                    for (char c : chars) {
                        Screen.wrapScreenError(() ->
                                        element.charTyped(c, modifiers),
                                "charTyped event handler", element.getClass().getCanonicalName());
                    }
                }

            }
        }
    }
     */

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
