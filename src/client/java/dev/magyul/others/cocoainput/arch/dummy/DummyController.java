package dev.magyul.others.cocoainput.arch.dummy;

import dev.magyul.others.cocoainput.CocoaInput;
import dev.magyul.others.cocoainput.plugin.CocoaInputController;
import dev.magyul.others.cocoainput.plugin.IMEOperator;
import dev.magyul.others.cocoainput.plugin.IMEReceiver;
import net.minecraft.client.gui.screen.Screen;

public class DummyController implements CocoaInputController {

    public DummyController() {
        CocoaInput.LOGGER.info("This is a dummy controller.");
    }

    @Override
    public IMEOperator generateIMEOperator(IMEReceiver ime) {
        return new DummyIMEOperator();
    }

    @Override
    public void screenOpenNotify(Screen screen) {
    }
}
