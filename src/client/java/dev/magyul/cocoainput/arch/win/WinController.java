package dev.magyul.cocoainput.arch.win;

import dev.magyul.cocoainput.CocoaInput;
import dev.magyul.cocoainput.plugin.CocoaInputController;
import dev.magyul.cocoainput.plugin.IMEOperator;
import dev.magyul.cocoainput.plugin.IMEReceiver;
import dev.magyul.cocoainput.util.Logger;
import dev.magyul.cocoainput.util.Rect;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.glfw.GLFWNativeWin32;

import java.io.IOException;
import java.lang.reflect.Field;

public class WinController implements CocoaInputController {
    static WinIMEOperator focusedOperator = null;


    Handle.PreeditCallback pc = (str, cursor, length) -> {
        if (focusedOperator != null) {
            Logger.debug("marked {} {} {}", str.toString(), cursor, length);
            focusedOperator.owner.setMarkedText(str.toString(), cursor, length);
        }
    };

    Handle.DoneCallback dc = str -> {
        if (focusedOperator != null) {
            Logger.debug("done ({})", str.toString());
            focusedOperator.owner.insertText(str.toString());
        }
    };

    Handle.RectCallback rc = ret -> {
        if (focusedOperator != null) {
            Logger.debug("Rect callback");
            Rect point = focusedOperator.owner.getRect();
            if (point == null) {
                point = Rect.ZERO;
            }
            point.writeMemory(ret, true);
            return 0;
        }
        return 1;
    };

    public WinController() {
        Logger.log("This is Windows Controller");
        try {
            CocoaInput.copyLibrary("libwincocoainput.dll", "win/libwincocoainput.dll");
        } catch (IOException ex) {
            CocoaInput.LOGGER.error("CocoaInput error", ex);
        }
        Handle.INSTANCE.initialize(GLFWNativeWin32.glfwGetWin32Window(MinecraftClient.getInstance().getWindow().getHandle()),
                pc, dc, rc, Logger.clangLog, Logger.clangError, Logger.clangDebug);
    }

    @Override
    public IMEOperator generateIMEOperator(IMEReceiver ime) {
        return new WinIMEOperator(ime);
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
        }
    }
}
