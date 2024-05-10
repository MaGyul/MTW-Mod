package dev.magyul.cocoainput.arch.darwin;

import dev.magyul.cocoainput.CocoaInput;
import dev.magyul.cocoainput.plugin.CocoaInputController;
import dev.magyul.cocoainput.plugin.IMEOperator;
import dev.magyul.cocoainput.plugin.IMEReceiver;
import net.minecraft.client.gui.screen.Screen;

import java.lang.reflect.Field;

public class DarwinController implements CocoaInputController {
    public DarwinController() throws Exception {
        CocoaInput.copyLibrary("libcocoainput.dylib", "darwin/libcocoainput.dylib");
        Handle.INSTANCE.initialize(CallbackFunction.Func_log, CallbackFunction.Func_error, CallbackFunction.Func_debug);
        CocoaInput.LOGGER.info("DarwinController has been initialized.");
    }

    @Override
    public IMEOperator generateIMEOperator(IMEReceiver ime) {
        return new DarwinIMEOperator(ime);
    }

    @Override
    public void screenOpenNotify(Screen screen) {
        try {
            Field wrapper = screen.getClass().getField("wrapper");
            wrapper.setAccessible(true);
            if (wrapper.get(screen) instanceof IMEReceiver)
                return;
        } catch (Exception ignored) {}
        Handle.INSTANCE.refreshInstance();
    }
}
