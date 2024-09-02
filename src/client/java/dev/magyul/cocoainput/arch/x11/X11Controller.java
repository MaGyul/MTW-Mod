package dev.magyul.cocoainput.arch.x11;

import com.sun.jna.Memory;
import dev.magyul.cocoainput.CocoaInput;
import dev.magyul.cocoainput.plugin.CocoaInputController;
import dev.magyul.cocoainput.plugin.IMEOperator;
import dev.magyul.cocoainput.plugin.IMEReceiver;
import dev.magyul.cocoainput.util.Logger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWNativeX11;

import java.io.IOException;
import java.lang.reflect.Field;

public class X11Controller implements CocoaInputController {
    static X11IMEOperator focusedOperator = null;

    Handle.DrawCallback c_draw = (caret, chg_first, chg_length, length, iswstring,
                                  rawstring, rawwstring, primary, secondary, tertiary) -> {
        Logger.debug("Javaside draw begin");
        String string = iswstring ? rawwstring.toString() : rawstring;

        if (focusedOperator != null) {
            GLFW.glfwSetKeyCallback(window, null);
            focusedOperator.owner.setMarkedText(string, caret, tertiary - secondary);
        }
        Logger.debug("Preedit:" + caret + " " + chg_first + " " + chg_length + " " + length + " " + primary + " "
                + secondary + " " + tertiary + " " + string);
        int[] point = { 600, 600 };
        Memory memory = new Memory(8L);
        memory.write(0L, point, 0, 2);
        Logger.debug("Javaside draw end");
        return memory;
    };
    Handle.DoneCallback c_done = () -> {
        Logger.debug("javaside preedit done");
        if (focusedOperator != null) {
            focusedOperator.owner.insertText("");
        }
        setupKeyboardEvent();
    };

    public static void setupKeyboardEvent() {
        var client = MinecraftClient.getInstance();
        client.keyboard.setup(window);
        GLFW.glfwSetCharModsCallback(window, (l, i, i1) -> client.execute(() -> {
            if (focusedOperator != null) {
                focusedOperator.owner.insertText(String.valueOf(Character.toChars(i)));
            } else {
                client.keyboard.onChar(l, i, i1);
            }
        }));
    }

    private static final long window = MinecraftClient.getInstance().getWindow().getHandle();

    public X11Controller() throws IOException {
        setupKeyboardEvent();
        Logger.log("This is X11 Controller");
        CocoaInput.copyLibrary("libx11cocoainput.so", "x11/libx11cocoainput.so");
        Logger.log("Call clang initializer");
        Handle.INSTANCE.initialize(window, GLFWNativeX11.glfwGetX11Window(window), c_draw, c_done,
                Logger.clangLog, Logger.clangError, Logger.clangDebug);
        Handle.INSTANCE.set_focus(0);
        Logger.log("Finished clang initializer");
        Logger.log("X11Controller finished initialize");
    }

    @Override
    public IMEOperator generateIMEOperator(IMEReceiver ime) {
        return new X11IMEOperator(ime);
    }

    @Override
    public void screenOpenNotify(Screen screen) {
        try {
            Field wrapper = screen.getClass().getField("wrapper");
            wrapper.setAccessible(true);
            if (wrapper.get(screen) instanceof IMEReceiver) return;
        } catch (Exception ignored) {}
        if (focusedOperator != null) {
            focusedOperator.setFocused(false);
            focusedOperator = null;
        }
    }
}
