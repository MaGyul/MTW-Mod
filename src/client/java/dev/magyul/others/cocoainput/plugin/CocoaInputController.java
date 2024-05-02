package dev.magyul.others.cocoainput.plugin;

import net.minecraft.client.gui.screen.Screen;

public interface CocoaInputController {
    IMEOperator generateIMEOperator(IMEReceiver ime);
    void screenOpenNotify(Screen screen);
}
