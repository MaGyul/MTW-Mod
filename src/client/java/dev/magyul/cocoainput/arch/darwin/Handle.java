package dev.magyul.cocoainput.arch.darwin;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;

public interface Handle extends Library {
    Handle INSTANCE = Native.load("cocoainput", Handle.class);

    void initialize(Callback log, Callback error, Callback debug);
    void refreshInstance();
    void addInstance(String uuid, CallbackFunction.Func_insertText insertText_p, CallbackFunction.Func_setMarkedText setMarkedText_p, CallbackFunction.Func_firstRectForCharacterRange firstRectForCharacterRange_p);
    void removeInstance(String uuid);
    void discardMarkedText(String uuid);
    void setIfReceiveEvent(String uuid,int yn);
    float invertYCoordinate(float y);
}
